package org.opengroove.jzbot;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.SystemMenuBar;

import net.sf.opengroove.common.proxystorage.ProxyStorage;
import net.sf.opengroove.common.utils.StringUtils;

import org.cheffo.jeplite.JEP;
import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;
import org.jibble.pircbot.PircBot;
import org.jibble.pircbot.User;
import org.opengroove.jzbot.commands.CommandListCommand;
import org.opengroove.jzbot.commands.ConfigCommand;
import org.opengroove.jzbot.commands.ExecCommand;
import org.opengroove.jzbot.commands.ExplainCommand;
import org.opengroove.jzbot.commands.FactoidCommand;
import org.opengroove.jzbot.commands.HelpCommand;
import org.opengroove.jzbot.commands.IsRestrictedCommand;
import org.opengroove.jzbot.commands.JoinCommand;
import org.opengroove.jzbot.commands.LeaveCommand;
import org.opengroove.jzbot.commands.MMCommand;
import org.opengroove.jzbot.commands.OpCommand;
import org.opengroove.jzbot.commands.ReconnectCommand;
import org.opengroove.jzbot.commands.RedefineCommand;
import org.opengroove.jzbot.commands.RegexCommand;
import org.opengroove.jzbot.commands.RestrictCommand;
import org.opengroove.jzbot.commands.ShutdownCommand;
import org.opengroove.jzbot.commands.StatusCommand;
import org.opengroove.jzbot.commands.SuperopCommand;
import org.opengroove.jzbot.commands.TriggerCommand;
import org.opengroove.jzbot.commands.UnrestrictCommand;
import org.opengroove.jzbot.eval.CaltechEvaluator;
import org.opengroove.jzbot.eval.JEvalEvaluator;
import org.opengroove.jzbot.eval.JepliteEvaluator;
import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.FactEntity;
import org.opengroove.jzbot.fact.FactParser;
import org.opengroove.jzbot.fact.FactQuota;
import org.opengroove.jzbot.fact.FactoidException;
import org.opengroove.jzbot.help.DefaultHelpProvider;
import org.opengroove.jzbot.help.FunctionHelpProvider;
import org.opengroove.jzbot.help.PropsHelpProvider;
import org.opengroove.jzbot.help.XMLHelpProvider;
import org.opengroove.jzbot.storage.*;
import org.opengroove.jzbot.utils.JZUtils;
import org.opengroove.jzbot.utils.Pastebin;

/**
 * jzbot authenticates off of hostmask.
 */
public class JZBot
{
    public static Protocol bot;
    
    public static File logsFolder = new File("storage/logs");
    
    public static Map<String, String> globalVariables = new HashMap<String, String>();
    
    public static ArrayList<HelpProvider> helpProviders = new ArrayList<HelpProvider>();
    
    public static Map<Integer, HttpServer> httpServers = new HashMap<Integer, HttpServer>();
    
    public static void startHttpServer(int port, String factoid)
    {
        try
        {
            synchronized (httpServers)
            {
                if (httpServers.get(port) != null)
                    throw new RuntimeException(
                            "A server with that port has already been started, "
                                    + "serving the factoid "
                                    + httpServers.get(port).getFactoid());
                verifyStartServer(port);
                HttpServer server = new HttpServer(port, factoid);
                httpServers.put(port, server);
                server.startServer();
            }
        }
        catch (Exception e)
        {
            throw new FactoidException(
                    "Exception occured while starting an http server on port " + port
                            + " with factoid " + factoid, e);
        }
    }
    
    public static void stopHttpServer(int port)
    {
        try
        {
            synchronized (httpServers)
            {
                HttpServer server = httpServers.get(port);
                if (server == null)
                    throw new RuntimeException("No such server by that port");
                server.stopServer();
                httpServers.remove(port);
            }
        }
        catch (Exception e)
        {
            throw new FactoidException(
                    "Exception occured while stopping an http server on port " + port);
        }
    }
    
    public static final File serverPortsFile = new File("storage", "serverports.txt");
    public static final File maxServersFile = new File("storage", "maxservers.txt");
    
    private static void verifyStartServer(int port)
    {
        if (!serverPortsFile.exists())
            throw new RuntimeException("HTTP servers are disabled. To enable them, create "
                    + "a file called serverports.txt in the bot's storage folder, and "
                    + "set its contents to be a regular expression that will match the "
                    + "port numbers you want to allow servers to be started on.");
        String regex = StringUtils.readFile(serverPortsFile);
        regex = regex.trim();
        if (!("" + port).matches(regex))
            throw new RuntimeException("Invalid port; the port has to match the regex "
                    + regex);
        int maxServers;
        if (maxServersFile.exists())
            maxServers = Integer.parseInt(StringUtils.readFile(maxServersFile).trim());
        else
            maxServers = 20;
        if (httpServers.size() >= maxServers)
            throw new RuntimeException("There are already " + httpServers.size()
                    + " servers started. This bot imposes a " + "maximum limit of "
                    + maxServers + " at a time.");
    }
    
    static
    {
        helpProviders.add(new DefaultHelpProvider());
        helpProviders.add(new FunctionHelpProvider());
        helpProviders.add(new PropsHelpProvider("docs/help.props"));
    }
    
    public static Map<String, Evaluator> evalEngines = new HashMap<String, Evaluator>();
    
    public static void registerEvalEngine(Evaluator engine)
    {
        evalEngines.put(engine.getName(), engine);
    }
    
    static
    {
        registerEvalEngine(new JepliteEvaluator());
        registerEvalEngine(new JEvalEvaluator());
        registerEvalEngine(new CaltechEvaluator());
    }
    
    public static Evaluator getEvalEngine(String name)
    {
        Evaluator engine = evalEngines.get(name);
        if (engine == null)
            throw new RuntimeException("Invalid evaluator engine name: "
                    + name
                    + ", expected one of "
                    + StringUtils.delimited(evalEngines.keySet().toArray(new String[0]),
                            ", "));
        return engine;
    }
    
    public static Evaluator getDefaultEvalEngine(String channel)
    {
        return getEvalEngine(ConfigVars.evalengine.get());
    }
    
    public static class FutureFactoid extends Thread
    {
        private int delay;
        private String channel;
        private ArgumentList arguments;
        private String sender;
        private String key;
        private FactQuota quota;
        public long startTime;
        
        public FutureFactoid(int delay, String channel, ArgumentList arguments,
                String sender, String key, FactQuota quota)
        {
            if (channel == null)
                throw new RuntimeException("Can't schedule future factoids in pm. "
                        + "Run this factoid at a channel.");
            this.delay = delay;
            this.channel = channel;
            this.arguments = arguments;
            this.sender = sender;
            this.key = key;
            this.quota = quota;
            startTime = System.currentTimeMillis() + (delay * 1000);
        }
        
        public void run()
        {
            try
            {
                startTime = System.currentTimeMillis() + (delay * 1000);
                Thread.sleep(delay * 1000);
            }
            catch (InterruptedException e)
            {
            }
            synchronized (futureFactoidLock)
            {
                if (futureFactoids.get(key) != this)
                    return;
                futureFactoids.remove(key);
                String result = doFactImport(channel, arguments, sender, true, quota,
                        ImportLevel.any);
                if (result.trim().equals(""))
                    return;
                if (result.startsWith("<ACTION>"))
                    bot.sendAction(channel, result.substring("<ACTION>".length()));
                else
                    bot.sendMessage(channel, result);
            }
        }
    }
    
    public static HashMap<String, FutureFactoid> futureFactoids = new HashMap<String, FutureFactoid>();
    public static final Object futureFactoidLock = new Object();
    public static boolean manualReconnect = false;
    public static final HashMap<String, Command> commands = new HashMap<String, Command>();
    // numeric 320: is signed on as account
    public static ProxyStorage<Storage> proxyStorage;
    public static Storage storage;
    
    public static Config config;
    public static boolean isRunning;
    
    public static void main(String[] args) throws Throwable
    {
        if (args.length > 0)
        {
            doWithArguments(args);
            return;
        }
        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            public void run()
            {
                System.out.println("JZBot has terminated.");
            }
        });
        start();
    }
    
    private static void doWithArguments(String[] args)
    {
        if (args[0].equals("help"))
        {
            System.out.println("JZBot is an IRC bot. If you have questions, connect");
            System.out.println("to irc.freenode.net and join channel ##jzbot.");
            System.out.println("To set up your bot, run \"jzbot setup <server> ");
            System.out.println("<port> <nick> <hostname> <password>\". <server>");
            System.out.println("is the IRC server to connect to. For example, this");
            System.out.println("could be \"irc.freenode.net\". <port> is the port");
            System.out.println("on the server to connect to. This is usually 6667.");
            System.out.println("<nick> is the nickname to use on that server. ");
            System.out.println("<hostname> is your hostname or hostmask on the IRC");
            System.out.println("server, which the bot will use to allow you to ");
            System.out.println("tell it to join channels, leave channels, create ");
            System.out.println("factoids, and so on. <password> is the password");
            System.out.println("you want the bot to use when connecting to the");
            System.out.println("server. <password> is entirely optional.");
            System.out.println("");
            System.out.println("If you set up the bot with incorrect information,");
            System.out.println("you can always run the setup command again.");
            System.out.println("");
            System.out.println("Once you've set up the bot successfully, run \"jzbot\"");
            System.out.println("to actually start your bot.");
        }
        else if (args[0].equals("setup"))
        {
            ArrayList<String> list = new ArrayList<String>(Arrays.asList(args));
            list.remove(0);
            args = list.toArray(new String[0]);
            if (args.length < 4 || args.length > 5)
            {
                System.out.println("\"jzbot setup\" expects either 4 or 5 "
                        + "arguments, but you provided " + args.length);
                System.out.println("arguments. See \"jzbot help\" for help.");
                return;
            }
            String server = args[0];
            String portString = args[1];
            String nick = args[2];
            String hostname = args[3];
            String password = (args.length > 4 ? args[4] : "");
            int port;
            try
            {
                port = Integer.parseInt(portString);
            }
            catch (NumberFormatException e)
            {
                System.out.println("You specified " + portString + " for the port, but");
                System.out.println("the port must be a number.");
                return;
            }
            if (port < 0 || port > 65535)
            {
                System.out.println("The port number you specified (" + port + "), should");
                System.out.println("have been within the range 0 - 65535, but it was not.");
                return;
            }
            System.out.println("Ok, it looks like the information you provided ");
            System.out.println("will work. Hang on a sec while I set everything up.");
            System.out.println("");
            initProxyStorage();
            config.setNick(nick);
            config.setPassword(password);
            config.setPort(port);
            config.setServer(server);
            if (storage.getOperator(hostname) == null)
            {
                Operator op = storage.createOperator();
                op.setHostname(hostname);
                storage.getOperators().add(op);
            }
            System.out.println("");
            System.out.println("JZBot has been successfully set up. Run \"jzbot\"");
            System.out.println("to start your bot.");
        }
        else
        {
            System.out.println("That's an invalid command. Try \"jzbot help\".");
        }
    }
    
    private static void loadCommands()
    {
        loadCommand(new CommandListCommand());
        loadCommand(new ConfigCommand());
        loadCommand(new ExecCommand());
        loadCommand(new ExplainCommand());
        loadCommand(new FactoidCommand());
        // loadCommand(new GoogleCommand());
        loadCommand(new HelpCommand());
        loadCommand(new IsRestrictedCommand());
        loadCommand(new JoinCommand());
        loadCommand(new LeaveCommand());
        // loadCommand(new LengthCommand());
        loadCommand(new MMCommand());
        loadCommand(new OpCommand());
        loadCommand(new ReconnectCommand());
        loadCommand(new RedefineCommand());
        loadCommand(new RegexCommand());
        loadCommand(new RestrictCommand());
        // loadCommand(new RouletteCommand());
        // loadCommand(new SayCommand());
        loadCommand(new ShutdownCommand());
        loadCommand(new StatusCommand());
        loadCommand(new SuperopCommand());
        loadCommand(new TriggerCommand());
        // loadCommand(new TTTCommand());
        loadCommand(new UnrestrictCommand());
        // loadCommand(new WeatherCommand());
    }
    
    private static void loadCommand(Command command)
    {
        commands.put(command.getName(), command);
    }
    
    private static void start() throws Throwable
    {
        logsFolder.mkdirs();
        initProxyStorage();
        if (config.getNick() == null || config.getPassword() == null
                || config.getServer() == null)
        {
            System.out.println("No connect info specified. JZBot will exit.");
            System.out.println("Run \"jzbot help\" for help on how to set up");
            System.out.println("JZBot and the connection info.");
            System.exit(0);
        }
        loadProtocol();
        loadCommands();
        loadCachedConfig();
        reloadRegexes();
        try
        {
            bot.setMessageDelay(Integer.parseInt(ConfigVars.delay.get()));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        bot.setLogin(config.getNick());
        bot.setName(config.getNick());
        bot.setVersion("JZBot -- http://jzbot.googlecode.com");
        bot.setAutoNickChange(true);
        try
        {
            bot.setEncoding(ConfigVars.charset.get());
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        System.out.println("connecting");
        bot.connect(config.getServer(), config.getPort(), config.getPassword());
        System.out.println("connected");
    }
    
    private static void initProxyStorage()
    {
        proxyStorage = new ProxyStorage<Storage>(Storage.class, new File("storage/db"));
        storage = proxyStorage.getRoot();
        config = storage.getConfig();
        if (config == null)
        {
            config = storage.createConfig();
            storage.setConfig(config);
        }
    }
    
    private static void loadCachedConfig()
    {
        configNolog = ConfigVars.nolog.get();
        try
        {
            configLogsize = Integer.parseInt(ConfigVars.logsize.get());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            configLogsize = 0;
        }
    }
    
    @SuppressWarnings("unchecked")
    private static void loadProtocol()
    {
        String protocolClass = config.getProtocol();
        System.out.println("Using protocol " + protocolClass);
        try
        {
            Class<? extends Protocol> classObject = (Class<? extends Protocol>) Class
                    .forName(protocolClass);
            bot = classObject.newInstance();
            bot.init();
        }
        catch (Exception e)
        {
            throw new RuntimeException("Exception occurred while loading protocol", e);
        }
    }
    
    public static void onJoin(String channel, String sender, String login, String hostname)
    {
        System.out.println("join detected on " + channel + " by " + sender);
        Channel chan = storage.getChannel(channel);
        if (chan == null)
            return;
        logEvent(channel, "joined", sender, login + "@" + hostname);
        if (sender.equals(bot.getNick()))
        {
            runNotificationFactoid(channel, chan, sender, "_selfjoin", null, true);
        }
        else
        {
            runNotificationFactoid(channel, chan, sender, "_onjoin", null, true);
        }
    }
    
    public static void onPart(String channel, String sender, String login, String hostname)
    {
        logEvent(channel, "left", sender, "Left the channel");
        runNotificationFactoid(channel, null, sender, "_onpart", null, true);
    }
    
    public static void onBeforeQuit(String sourceNick, String sourceLogin,
            String sourceHostname, String reason)
    {
        for (String channel : bot.getChannels())
        {
            if (getUser(channel, sourceNick) != null)
            {
                logEvent(channel, "left", sourceNick, "Quit: " + reason);
                runNotificationFactoid(channel, null, sourceNick, "_onquit", null, true);
            }
        }
    }
    
    public static void onTopic(String channel, String topic, String setBy, long date,
            boolean changed)
    {
        if (changed)
        {
            logEvent(channel, "topic", setBy, topic);
            runNotificationFactoid(channel, null, setBy, "_ontopic", new String[]
            {
                    topic, "" + date
            }, true);
        }
    }
    
    public static void onMode(String channel, String sourceNick, String sourceLogin,
            String sourceHostname, String mode)
    {
        logEvent(channel, "mode", sourceNick, mode);
        runNotificationFactoid(channel, null, sourceNick, "_onmode", new String[]
        {
            mode
        }, true);
    }
    
    public static void onNickChange(String oldNick, String login, String hostname,
            String newNick)
    {
        for (String channel : bot.getChannels())
        {
            if (getUser(channel, newNick) != null)
            {
                logEvent(channel, "nick", oldNick, newNick);
                runNotificationFactoid(channel, null, newNick, "_onrename", new String[]
                {
                        oldNick, newNick
                }, true);
            }
        }
    }
    
    private static void runNotificationFactoid(String channelName, Channel chan,
            String sender, String factname, String[] args, boolean timed)
    {
        if (!factname.startsWith("_"))
            System.err.println("Factoid notification name \"" + factname
                    + "\" doesn't start with an underscore");
        if (args == null)
            args = new String[0];
        ArrayList<Factoid> facts = new ArrayList<Factoid>();
        if (channelName == null)
        {
            facts.addAll(Arrays.asList(storage.searchFactoids(factname)));
            facts.addAll(Arrays.asList(storage.searchFactoids(factname + "_*")));
        }
        else
        {
            if (chan == null)
                chan = storage.getChannel(channelName);
            if (chan == null)
                return;
            facts.addAll(Arrays.asList(chan.searchFactoids(factname)));
            facts.addAll(Arrays.asList(chan.searchFactoids(factname + "_*")));
            facts.addAll(Arrays.asList(storage.searchFactoids("_chan" + factname)));
            facts.addAll(Arrays.asList(storage.searchFactoids("_chan" + factname + "_*")));
        }
        TimedKillThread tkt = new TimedKillThread(Thread.currentThread());
        tkt.maxRunTime = 40 * 1000;
        if (timed)
            tkt.start();
        try
        {
            for (Factoid f : facts)
            {
                if (f != null)
                {
                    incrementIndirectRequests(f);
                    String factValue = safeRunFactoid(f, channelName, sender, args, true,
                            new HashMap<String, String>());
                    String pseudoChannel = channelName;
                    if (pseudoChannel == null)
                        pseudoChannel = ConfigVars.primary.get();
                    if (factValue.trim().equals(""))
                        ;
                    else if (factValue.startsWith("<ACTION>"))
                        bot.sendAction(pseudoChannel, factValue.substring("<ACTION>"
                                .length()));
                    else
                        bot.sendMessage(pseudoChannel, factValue);
                }
            }
        }
        finally
        {
            tkt.active = false;
        }
    }
    
    public static void incrementIndirectRequests(Factoid f)
    {
        f.setIndirectRequests(f.getIndirectRequests() + 1);
    }
    
    public static void incrementDirectRequests(Factoid f)
    {
        // System.out.println("Incrementing direct requests for " +
        // f.getName());
        f.setDirectRequests(f.getDirectRequests() + 1);
        // System.out.println("incremented");
    }
    
    /**
     * Runs the factoid specified, outputting to the string specified.
     * 
     * @param factoid
     *            The factoid to run
     * @param channel
     *            The channel that it was run on
     * @param sender
     *            The sender of the factoid request
     */
    public static String runFactoid(Factoid factoid, String channel, String sender,
            String[] args, Map<String, String> vars, boolean allowRestricted,
            FactQuota quota)
    {
        if (quota == null)
            quota = new FactQuota();
        if (allowRestricted == false && factoid.isRestricted())
            throw new FactoidException("The factoid " + factoid.getName()
                    + " is restricted. Only ops and superops "
                    + "can run it, as well as the bot itself.");
        for (int i = 0; i < args.length; i++)
        {
            vars.put("" + (i + 1), args[i]);
        }
        String cAppend = "";
        for (int i = args.length - 1; i >= 0; i--)
        {
            cAppend = args[i] + ((i == args.length - 1) ? "" : " ") + cAppend;
            vars.put("" + (i + 1) + "-", cAppend);
        }
        vars.put("0", sender);
        vars.put("who", sender);
        if (channel != null)
            vars.put("channel", channel);
        else
            vars.put("channel", "none");
        vars.put("self", bot.getNick());
        vars.put("source", channel == null ? sender : channel);
        String text = factoid.getValue();
        FactEntity parsedFactoid = FactParser.parse(text, factoid.getName());
        FactContext context = new FactContext();
        context.setQuota(quota);
        context.setChannel(channel);
        context.setSender(sender);
        context.setGlobalVars(globalVariables);
        context.setLocalVars(vars);
        context.setSelf(bot.getNick());
        // Now we actually run the factoid.
        String result = parsedFactoid.resolve(context);
        // The factoid has been run. Now we return the value.
        boolean isAction = context.isAction();
        return (isAction ? "<ACTION>" : "") + result.toString();
    }
    
    public static enum ImportLevel
    {
        any, exact, global
    }
    
    public static String doFactImport(String channel, ArgumentList arguments,
            String sender, boolean allowRestricted, FactQuota quota, ImportLevel level)
    {
        return doFactImport(channel, arguments, sender, allowRestricted, quota, level, null);
    }
    
    public static String doFactImport(String channel, ArgumentList arguments,
            String sender, boolean allowRestricted, FactQuota quota, ImportLevel level,
            Map<String, String> cascadingVars)
    {
        Factoid f = null;
        boolean channelSpecific = false;
        if (channel != null)
        {
            f = JZBot.storage.getChannel(channel).getFactoid(arguments.get(0));
            if (f != null)
                channelSpecific = true;
        }
        if (f == null)
        {
            f = JZBot.storage.getFactoid(arguments.get(0));
        }
        if (f == null)
            throw new RuntimeException("Invalid import factoid " + arguments.get(0));
        Map<String, String> varMap = new HashMap<String, String>();
        if (cascadingVars != null)
            varMap.putAll(cascadingVars);
        incrementIndirectRequests(f);
        return runFactoid(f, channel, sender, arguments.subList(1).evalToArray(), varMap,
                allowRestricted, quota);
    }
    
    public static void onKick(String channel, String kickerNick, String kickerLogin,
            String kickerHostname, String recipientNick, String reason)
    {
        logEvent(channel, "kick", kickerNick, recipientNick + " " + reason);
        runNotificationFactoid(channel, null, kickerNick, "_onkick", new String[]
        {
                recipientNick, reason, kickerNick
        }, true);
        if (recipientNick.equals(bot.getNick()))
            bot.joinChannel(channel);
    }
    
    public static void onMessage(String channel, String sender, String login,
            String hostname, String message)
    {
        logEvent(channel, "message", sender, message);
        TimedKillThread tkt = new TimedKillThread(Thread.currentThread());
        tkt.start();
        try
        {
            System.out
                    .println("Message from " + channel + " by " + sender + ": " + message);
            Channel chan = storage.getChannel(channel);
            if (chan == null)
            {
                System.out.println("No matching channel");
                return;
            }
            boolean processFactoids = processChannelRegex(channel, sender, hostname,
                    message, false);
            String trigger = chan.getTrigger();
            if (trigger != null && message.startsWith(trigger))
            {
                try
                {
                    System.out.println("running message command");
                    runMessageCommand(channel, false, sender, hostname, login, message
                            .substring(trigger.length()), processFactoids);
                }
                catch (Throwable e)
                {
                    e.printStackTrace();
                    JZBot.bot.sendMessage(channel, "Internal upper-propegation error: "
                            + pastebinStack(e));
                }
            }
            else
            {
                System.out.println("Incorrect trigger");
            }
        }
        catch (FactTimeExceededError e)
        {
            JZBot.bot.sendMessage(channel, "Time exceeded: " + pastebinStack(e));
        }
        finally
        {
            tkt.active = false;
        }
    }
    
    public static boolean processChannelRegex(String channel, String sender,
            String hostname, String message, boolean action)
    {
        try
        {
            boolean factOverridden = false;
            synchronized (regexLock)
            {
                List<String> channelList = regexCache.get(channel);
                if (channelList == null)
                    return true;
                for (String regex : channelList)
                {
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(message);
                    if (!matcher.find())
                        continue;
                    /*
                     * We found something.
                     */
                    OverrideStatus override = runRegex(channel, sender, hostname, message,
                            matcher, regex, action);
                    if (override == OverrideStatus.override)
                        return false;
                    else if (override == OverrideStatus.factoverride)
                        factOverridden = true;
                }
            }
            if (factOverridden)
                return false;
            return true;
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            bot.sendMessage(channel, "Pre-process regex error: " + pastebinStack(e));
            return true;
        }
    }
    
    public static enum OverrideStatus
    {
        override, factoverride, none
    }
    
    /**
     * 
     * @param channel
     * @param sender
     * @param hostname
     * @param message
     * @param action
     * @param regex
     * @return True if this overrides, false if it doesn't
     */
    private static OverrideStatus runRegex(String channel, String sender, String hostname,
            String message, Matcher matcher, String regexValue, boolean action)
    {
        Channel c = storage.getChannel(channel);
        if (c == null)
            return OverrideStatus.none;
        Regex regex = c.getRegex(regexValue);
        if (regex == null)
            return OverrideStatus.none;
        Factoid f = c.getFactoid(regex.getFactoid());
        if (f == null)
            f = JZBot.storage.getFactoid(regex.getFactoid());
        if (f == null)
        {
            bot.sendMessage(channel, "Invalid factoid in regex " + regexValue + ": "
                    + regex.getFactoid());
            return OverrideStatus.none;
        }
        HashMap<String, String> vars = new HashMap<String, String>();
        vars.put("regex", regexValue);
        vars.put("original", message);
        vars.put("matched", matcher.group(0));
        vars.put("hostname", hostname);
        vars.put("isaction", action ? "1" : "0");
        String[] strings = new String[matcher.groupCount()];
        for (int i = 1; i <= matcher.groupCount(); i++)
        {
            strings[i - 1] = matcher.group(i);
        }
        incrementIndirectRequests(f);
        String factValue = safeRunFactoid(f, channel, sender, strings, true, vars);
        if (factValue.trim().equals(""))
            ;
        else if (factValue.startsWith("<ACTION>"))
            bot.sendAction(channel, factValue.substring("<ACTION>".length()));
        else
            bot.sendMessage(channel, factValue);
        if ("true".equalsIgnoreCase(vars.get("__internal_override")))
            return OverrideStatus.override;
        else if ("true".equalsIgnoreCase(vars.get("__fact_override")))
            return OverrideStatus.factoverride;
        return OverrideStatus.none;
    }
    
    public static void onAction(String sender, String login, String hostname,
            String channel, String action)
    {
        if (!(channel.startsWith("#")))
            return;
        logEvent(channel, "action", sender, action);
        TimedKillThread tkt = new TimedKillThread(Thread.currentThread());
        tkt.start();
        try
        {
            processChannelRegex(channel, sender, hostname, action, true);
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            bot.sendMessage(sender, "Exception while processing action: "
                    + pastebinStack(t));
        }
        finally
        {
            tkt.active = false;
        }
    }
    
    private static void runMessageCommand(String channel, boolean pm, String sender,
            String hostname, String username, String message, boolean processFactoids)
    {
        String[] commandSplit = message.split(" ", 2);
        String command = commandSplit[0];
        String commandArguments = (commandSplit.length == 1 ? "" : commandSplit[1]);
        /*
         * We'll check to see if the text entered is a command. This is done before a
         * check to see if we're supposed to process factoids so that regexes can't
         * override commands.
         */
        Command c = commands.get(command);
        if (c != null)
        {
            System.out.println("Command: " + c.getName());
            try
            {
                threadLocalUsername.set(username);
                c.run(channel, pm, sender, hostname, commandArguments);
            }
            catch (Exception e)
            {
                if (e instanceof ResponseException)
                {
                    bot.sendMessage(pm ? sender : channel, ((ResponseException) e)
                            .getMessage());
                }
                else
                {
                    e.printStackTrace();
                    bot.sendMessage(pm ? sender : channel,
                            "An error occured while running the command " + command + ": "
                                    + pastebinStack(e));
                }
            }
            return;
        }
        /*
         * If we get here, then the text isn't a command. We'll check to see if it's a
         * factoid.
         * 
         * Our first check will be for a channel-specific factoid.
         */
        if (!processFactoids)
            return;
        if (channel != null)
        {
            Channel cn = storage.getChannel(channel);
            if (cn != null)
            {
                Factoid f = cn.getFactoid(command);
                if (f != null)
                {
                    System.out.println("channel-specific factoid");
                    if (f.isLibrary())
                    {
                        bot.sendMessage(pm ? sender : channel,
                                "That factoid is a library factoid. It can only be run "
                                        + "by importing it, by creating a regex "
                                        + "that uses it, by using it as "
                                        + "a trigger, and so on. Run \"factoid unlibrary "
                                        + command
                                        + "\" if you want to remove this factoid's "
                                        + "library status.");
                        return;
                    }
                    incrementDirectRequests(f);
                    String factValue;
                    factValue = safeRunFactoid(f, channel, sender, commandArguments
                            .split(" "), isOp(channel, hostname),
                            new HashMap<String, String>());
                    System.out.println("fact value: " + factValue);
                    if (factValue.trim().equals(""))
                        ;
                    else if (factValue.startsWith("<ACTION>"))
                        bot.sendAction((pm ? sender : channel), factValue
                                .substring("<ACTION>".length()));
                    else
                        bot.sendMessage((pm ? sender : channel), factValue);
                    return;
                }
            }
        }
        /*
         * Now we'll check for a global factoid.
         */
        Factoid f = storage.getFactoid(command);
        if (f != null)
        {
            System.out.println("global factoid");
            if (f.isLibrary())
            {
                bot.sendMessage(pm ? sender : channel,
                        "That factoid is a library factoid. It can only be run "
                                + "by importing it, by creating a regex "
                                + "that uses it, by using it as "
                                + "a trigger, and so on. Run \"factoid unlibrary "
                                + command + "\" if you want to remove this factoid's "
                                + "library status.");
                return;
            }
            incrementDirectRequests(f);
            System.out.println("requests incremented");
            String factValue;
            System.out.println("calculating fact value");
            factValue = safeRunFactoid(f, channel, sender, commandArguments.split(" "),
                    isOp(channel, hostname), new HashMap<String, String>());
            System.out.println("fact value: " + factValue);
            if (factValue.trim().equals(""))
                System.out.println("Empty value; doing nothing");
            else if (factValue.startsWith("<ACTION>"))
                bot.sendAction((pm ? sender : channel), factValue.substring("<ACTION>"
                        .length()));
            else
            {
                System.out
                        .println("sending global message " + channel + " to " + factValue);
                bot.sendMessage((pm ? sender : channel), factValue);
            }
            return;
        }
        System.out.println("invalid command");
        doInvalidCommand(pm, channel, sender);
    }
    
    /**
     * Runs the specified factoid, returning its output. If an exception is thrown while
     * running the factoid, the exception's stack trace, along with some additional
     * information, is sent to pastebin.com, and an error message (including a url to the
     * pastebin post) returned instead of the factoid's output.
     * 
     * @param f
     *            The factoid to run
     * @param channel
     *            The name of the channel that the factoid is being run at
     * @param sender
     *            The nickname of the user that is running the factoid
     * @param arguments
     *            The arguments to the factoid
     * @param allowRestricted
     *            True to allow restricted factoids
     * @param vars
     *            The local variables to use. Extra variables will be added for function
     *            arguments and stuff like %channel%.
     * @return The output of the factoid, or an error message containing a pastebin url if
     *         the factoid threw an exception while running
     */
    public static String safeRunFactoid(Factoid f, String channel, String sender,
            String[] arguments, boolean allowRestricted, Map<String, String> vars)
    {
        String factValue;
        try
        {
            factValue = runFactoid(f, channel, sender, arguments, vars, allowRestricted,
                    null);
        }
        catch (FactoidException e)
        {
            factValue = "Syntax exception while running factoid: " + pastebinStack(e);
        }
        catch (Exception e)
        {
            factValue = "External exception while running factoid: " + pastebinStack(e);
        }
        catch (FactTimeExceededError e)
        {
            factValue = "The factoid took too long to run: " + pastebinStack(e);
        }
        catch (StackOverflowError e)
        {
            factValue = "Internal stack overflow error: " + e;
        }
        return factValue;
    }
    
    public static String pastebinStack(Throwable e)
    {
        e.printStackTrace();
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw, true));
        String eString = sw.toString();
        if (e instanceof FactoidException)
            eString = ((FactoidException) e).createFactoidStackTrace()
                    + "\n\nJava stack trace:\n\n" + eString;
        try
        {
            return "http://pastebin.com/"
                    + Pastebin.createPost("jzbot", eString, Pastebin.Duration.DAY, "");
        }
        catch (Exception e2)
        {
            return "(pastebin service unavailable)";
        }
    }
    
    private static void doInvalidCommand(boolean pm, String channel, String sender)
    {
        Channel c = storage.getChannel(channel);
        String notfoundFact = ConfigVars.notfound.get();
        if (notfoundFact.trim().equals("") && c != null)
        {
            bot.sendMessage(pm ? sender : channel, "Huh? (pm \"help\" for more info)");
        }
        else if (notfoundFact.trim().equals(""))
        {
            bot.sendMessage(pm ? sender : channel, "Huh? (pm \"help\" for more info)");
        }
        else
        {
            try
            {
                Factoid f = null;
                if (c != null)
                    f = c.getFactoid(notfoundFact);
                if (f == null)
                    f = storage.getFactoid(notfoundFact);
                if (f == null)
                    throw new RuntimeException("The factoid " + notfoundFact
                            + " does not exist.");
                String factValue = safeRunFactoid(f, channel, sender, new String[0], true,
                        new HashMap<String, String>());
                if (factValue.trim().equals(""))
                    bot.sendMessage(pm ? sender : channel,
                            "Notfound factoid didn't output anything");
                else if (factValue.startsWith("<ACTION>"))
                    bot.sendAction(pm ? sender : channel, factValue.substring("<ACTION>"
                            .length()));
                else
                    bot.sendMessage(pm ? sender : channel, factValue);
            }
            catch (Throwable t)
            {
                bot.sendMessage(pm ? sender : channel, "Syntax error in notfound factoid: "
                        + pastebinStack(t));
            }
        }
    }
    
    public static void onConnect()
    {
        new Thread()
        {
            public void run()
            {
                for (Channel channel : storage.getChannels().isolate())
                {
                    if (!channel.isSuspended())
                    {
                        try
                        {
                            Thread.sleep(2500);
                        }
                        catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                        System.out.println("joining " + channel.getName());
                        bot.joinChannel(channel.getName());
                    }
                }
                try
                {
                    Thread.sleep(2500);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                runNotificationFactoid(null, null, bot.getNick(), "_onready",
                        new String[0], true);
            }
        }.start();
    }
    
    public static void onDisconnect()
    {
        System.out.println("on disconnect");
        try
        {
            elevatedOpMap.clear();
            elevatedSuperopList.clear();
            proxyStorage.close();
            synchronized (httpServers)
            {
                for (int port : httpServers.keySet())
                {
                    httpServers.get(port).stopServer();
                }
                httpServers.clear();
            }
            initProxyStorage();
            reloadRegexes();
            loadCachedConfig();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        new Thread()
        {
            
            public void run()
            {
                int attempts = 0;
                while (true)
                {
                    try
                    {
                        attempts++;
                        int time;
                        if (attempts < 5)
                            time = 1;
                        else if (attempts < 10)
                            time = 5;
                        else if (attempts < 20)
                            time = 15;
                        else if (attempts < 40)
                            time = 30;
                        else if (attempts < 70)
                            time = 60;
                        else if (attempts < 100)
                            time = 120;
                        else
                            time = 240;
                        // This is to make sure that we don't flood ourselves
                        // off again after we join
                        if (manualReconnect)
                            manualReconnect = false;
                        else
                            time += 30;
                        Thread.sleep(time * 1000);
                        bot.reconnect();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                        continue;
                    }
                    return;
                }
            }
        }.start();
    }
    
    public static void onPrivateMessage(String sender, String login, String hostname,
            String message)
    {
        TimedKillThread tkt = new TimedKillThread(Thread.currentThread());
        tkt.start();
        try
        {
            System.out.println("pm from " + sender + ": " + message);
            String channel = null;
            if (message.startsWith("#") && message.contains(" "))
            {
                channel = message.substring(0, message.indexOf(" "));
                message = message.substring(message.indexOf(" ") + 1);
            }
            try
            {
                runMessageCommand(channel, true, sender, hostname, login, message, true);
            }
            catch (Throwable e)
            {
                e.printStackTrace();
                bot.sendMessage(sender, "Internal upper-propegating pm exception: "
                        + pastebinStack(e));
            }
        }
        catch (FactTimeExceededError e)
        {
            JZBot.bot.sendMessage(sender, "Time exceeded: " + pastebinStack(e));
        }
        finally
        {
            tkt.active = false;
        }
        
    }
    
    private static Map<String, ArrayList<String>> elevatedOpMap = new HashMap<String, ArrayList<String>>();
    
    private static ArrayList<String> elevatedSuperopList = new ArrayList<String>();
    
    public static void elevate(String hostname, String channel)
    {
        if (channel.equals(ConfigVars.primary.get()))
            elevatedSuperopList.add(hostname);
        else
        {
            ArrayList<String> list = elevatedOpMap.get(channel);
            if (list == null)
            {
                list = new ArrayList<String>();
                elevatedOpMap.put(channel, list);
            }
            list.add(hostname);
        }
    }
    
    public static boolean isOp(String channel, String hostname)
    {
        if (isSuperop(hostname))
            return true;
        Channel c = storage.getChannel(channel);
        if (c == null)
            return false;
        if (elevatedOpMap.get(channel) != null
                && elevatedOpMap.get(channel).contains(hostname))
            return true;
        return c.getOperator(hostname) != null;
    }
    
    public static boolean isSuperop(String hostname)
    {
        if (elevatedSuperopList.contains(hostname))
            return true;
        return storage.getOperator(hostname) != null;
    }
    
    public static void verifyOp(String channel, String hostname)
    {
        if (!isOp(channel, hostname))
            throw new ResponseException(
                    "You are not an op, so you don't have permission to run this command.");
    }
    
    public static void verifySuperop(String hostname)
    {
        if (!isSuperop(hostname))
            throw new ResponseException(
                    "You are not a superop, so you don't have permission to run this command.");
    }
    
    /**
     * Gets the nickname for the specified user. If the url is a nickname url, then the
     * nickname is simply taken from the url. If the url is an authenticated url, then the
     * corresponding protocol is asked for the user's nickname.
     * 
     * @param user
     * @return
     */
    public static String getNickname(URI user)
    {
        return null;
    }
    
    /**
     * Gets the authenticated name for the specified user. If the user is an authenticated
     * url, then the authenticated name is simply taken from the url. If the url is a
     * nickname url, then the protocol is asked for the user's authenticated name.<br/>
     * <br/>
     * 
     * Due to the fact that nicknames can change frequently while authenticated names
     * generally won't, this should be called as soon after receiving a nickname as
     * possible, to avoid the wrong authenticated name being obtained.<br/> <br/>
     * 
     * It's possible that the user's authenticated name and the user's nickname are the
     * same.
     * 
     * @param user
     * @return The user's authenticated name, or null if their protocol reports that the
     *         user is not authenticated
     */
    public static String getAuthname(URI user)
    {
        return null;
    }
    
    /**
     * Converts the specified nickname url to an authenticated name url, or null if the
     * user specified is not authenticated. If the url is already an authenticated url,
     * then it is returned as-is.
     * 
     * @param user
     * @return
     */
    public static URI toAuthForm(URI user)
    {
        return null;
    }
    
    public static User getUser(String channel, String nick)
    {
        User[] users = bot.getUsers(channel);
        for (User u : users)
        {
            if (nick.equalsIgnoreCase(u.getNick()))
                return u;
        }
        return null;
    }
    
    public static String evaluateEquation(String toEval, String channel)
    {
        return evaluateEquation(toEval, channel, ConfigVars.evalengine.get());
    }
    
    public static String evaluateEquation(String toEval, String channel, String engineName)
    {
        try
        {
            Evaluator engine = getEvalEngine(engineName);
            return engine.evaluate(toEval);
        }
        catch (Exception e)
        {
            throw new FactoidException("Exception while evaluating " + toEval
                    + " with engine " + ConfigVars.evalengine.get(), e);
        }
    }
    
    /**
     * Returns a string representing this double rounded to 8 decimal points, and with no
     * decimal point if one is not needed.
     * 
     * @param value
     *            The value to round
     * @return The value
     */
    public static String toRoundedString(double value)
    {
        BigDecimal d = new BigDecimal(value);
        d = d.movePointRight(9);
        d = new BigDecimal(d.toBigInteger());
        d = d.movePointLeft(9);
        d = d.stripTrailingZeros();
        if (d.doubleValue() == 0)
            return "0";
        return d.toPlainString();
    }
    
    public static void sendDelimited(String[] array, String delimiter, String recipient)
    {
        JZUtils.ircSendDelimited(array, delimiter, bot, recipient);
    }
    
    public static Factoid getChannelFactoid(String channelName, String factoid)
    {
        Channel channel = storage.getChannel(channelName);
        if (channel == null)
            throw new ResponseException("No such channel: " + channel);
        return channel.getFactoid(factoid);
    }
    
    public static Factoid getGlobalFactoid(String factoid)
    {
        return storage.getFactoid(factoid);
    }
    
    private static ThreadLocal<String> threadLocalUsername = new ThreadLocal<String>();
    
    public static String getThreadLocalUsername()
    {
        return threadLocalUsername.get();
    }
    
    public static void setCurrentCharset(String charset)
    {
        try
        {
            Charset.forName(charset);
        }
        catch (Exception e)
        {
            throw new ResponseException(
                    "That is not a charset supported on this platform.", e);
        }
        validateWorkableCharset(charset);
        try
        {
            bot.setEncoding(charset);
        }
        catch (Exception e)
        {
            throw new ResponseException(
                    "That is not a charset supported on this platform. "
                            + "(in bot.setEncoding(String))", e);
        }
    }
    
    private static void validateWorkableCharset(String charset)
    {
        byte[] bytes = new byte[]
        {
                '~', 's', 'o', 'm', 'e', 't', 'h', 'i', 'n', 'g'
        };
        try
        {
            String s = new String(bytes, charset);
            if (s.length() != 10)
                throw new ResponseException("Charset failed length validation");
            if (s.charAt(0) != '~')
                throw new ResponseException("Charset failed prefix validation");
            if (s.charAt(4) != 'e')
                throw new ResponseException("Charset failed content validation");
        }
        catch (UnsupportedEncodingException e)
        {
            throw new ResponseException(
                    "That is not a charset supported on this platform. "
                            + "(in new String(byte[],String))", e);
        }
    }
    
    private static Map<String, List<String>> regexCache = new HashMap<String, List<String>>();
    
    private static Object regexLock = new Object();
    
    private static String configNolog;
    
    private static int configLogsize = 0;
    
    public static final long startedAtTime = System.currentTimeMillis();
    
    public static void reloadRegexes()
    {
        synchronized (regexLock)
        {
            regexCache.clear();
            for (Channel c : storage.getChannels().isolate())
            {
                ArrayList<String> list = new ArrayList<String>();
                regexCache.put(c.getName(), list);
                for (Regex regex : c.getRegularExpressions().isolate())
                {
                    list.add(regex.getExpression());
                }
            }
        }
    }
    
    public static boolean isChannelOp(String channel, String sender)
    {
        User[] users = bot.getUsers(channel);
        for (User user : users)
        {
            if (user.getNick().equals(sender))
            {
                if (user.isOp())
                    return true;
            }
        }
        return false;
    }
    
    public static void logEvent(String channel, String event, String nick, String details)
    {
        details = details.replace("\r", "").replace("\n", "");
        try
        {
            if (StringUtils.isMemberOf(channel, configNolog.split("\\|")))
                return;
            String data = event + " " + System.currentTimeMillis() + " " + nick + " "
                    + details;
            File logFile = new File(logsFolder, channel);
            if (!logFile.exists())
                if (!logFile.createNewFile())
                    throw new Exception("Couldn't create new log file.");
            String content = StringUtils.readFile(logFile);
            if ((!content.endsWith("\n")) && content.length() > 0)
                content += "\n";
            content += data;
            while (content.length() > configLogsize)
            {
                if (content.length() == 0)
                    break;
                int newlinePlace = content.indexOf('\n');
                if (newlinePlace == -1)
                    newlinePlace = content.length() - 1;
                content = content.substring(newlinePlace + 1);
            }
            StringUtils.writeFile(content, logFile);
        }
        catch (Exception e)
        {
            new Exception("Exception while writing data to channel logs", e)
                    .printStackTrace();
        }
    }
    
    public static File[] listLocalFactpackFiles()
    {
        File[] files = new File("factpacks").listFiles(new FileFilter()
        {
            
            @Override
            public boolean accept(File file)
            {
                return file.getName().endsWith(".jzf");
            }
        });
        Arrays.sort(files, new Comparator<File>()
        {
            
            @Override
            public int compare(File o1, File o2)
            {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });
        return files;
    }
    
    public static File getLocalFactpackFile(String canonicalName)
    {
        for (File file : listLocalFactpackFiles())
        {
            Factpack pack = Factpack.parse(StringUtils.readFile(file));
            if (pack.name.equals(canonicalName))
                return file;
        }
        return null;
    }
}
