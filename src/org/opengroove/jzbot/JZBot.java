package org.opengroove.jzbot;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import net.sf.opengroove.common.proxystorage.ProxyStorage;
import net.sf.opengroove.common.utils.StringUtils;

import org.cheffo.jeplite.JEP;
import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;
import org.jibble.pircbot.PircBot;
import org.jibble.pircbot.User;
import org.opengroove.jzbot.commands.CommandListCommand;
import org.opengroove.jzbot.commands.ConfigCommand;
import org.opengroove.jzbot.commands.ExplainCommand;
import org.opengroove.jzbot.commands.FactoidCommand;
import org.opengroove.jzbot.commands.GoogleCommand;
import org.opengroove.jzbot.commands.HelpCommand;
import org.opengroove.jzbot.commands.IsRestrictedCommand;
import org.opengroove.jzbot.commands.JoinCommand;
import org.opengroove.jzbot.commands.JoinMessageCommand;
import org.opengroove.jzbot.commands.LeaveCommand;
import org.opengroove.jzbot.commands.LengthCommand;
import org.opengroove.jzbot.commands.MMCommand;
import org.opengroove.jzbot.commands.OpCommand;
import org.opengroove.jzbot.commands.ReconnectCommand;
import org.opengroove.jzbot.commands.RegexCommand;
import org.opengroove.jzbot.commands.RestrictCommand;
import org.opengroove.jzbot.commands.RouletteCommand;
import org.opengroove.jzbot.commands.SayCommand;
import org.opengroove.jzbot.commands.ShutdownCommand;
import org.opengroove.jzbot.commands.SuperopCommand;
import org.opengroove.jzbot.commands.TTTCommand;
import org.opengroove.jzbot.commands.TriggerCommand;
import org.opengroove.jzbot.commands.UnrestrictCommand;
import org.opengroove.jzbot.commands.WeatherCommand;
import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.FactEntity;
import org.opengroove.jzbot.fact.FactParser;
import org.opengroove.jzbot.fact.FactQuota;
import org.opengroove.jzbot.fact.FactoidException;
import org.opengroove.jzbot.help.DefaultHelpProvider;
import org.opengroove.jzbot.help.FunctionHelpProvider;
import org.opengroove.jzbot.storage.*;
import org.opengroove.jzbot.utils.JZUtils;
import org.opengroove.jzbot.utils.Pastebin;

/**
 * jzbot authenticates off of hostmask.
 */
public class JZBot extends PircBot
{
    public static Map<String, String> globalVariables = new HashMap<String, String>();
    
    public static ArrayList<HelpProvider> helpProviders = new ArrayList<HelpProvider>();
    
    static
    {
        helpProviders.add(new DefaultHelpProvider());
        helpProviders.add(new FunctionHelpProvider());
    }
    
    public static Map<String, Evaluator> evalEngines = new HashMap<String, Evaluator>();
    
    public static void registerEvalEngine(Evaluator engine)
    {
        evalEngines.put(engine.getName(), engine);
    }
    
    static
    {
        registerEvalEngine(new JepEvaluator());
        registerEvalEngine(new EvalEvaluator());
    }
    
    public static Evaluator getEvalEngine(String name)
    {
        Evaluator engine = evalEngines.get(name);
        if (engine == null)
            throw new RuntimeException("Invalid evaluator engine name: "
                    + name
                    + ", expected one of "
                    + StringUtils.delimited(evalEngines.keySet().toArray(
                            new String[0]), ", "));
        return engine;
    }
    
    public static Evaluator getDefaultEvalEngine(String channel)
    {
        return getEvalEngine(config.getEvalEngine());
    }
    
    public static class FutureFactoid extends Thread
    {
        private int delay;
        private String channel;
        private ArgumentList arguments;
        private String sender;
        private String key;
        private FactQuota quota;
        
        public FutureFactoid(int delay, String channel, ArgumentList arguments,
                String sender, String key, FactQuota quota)
        {
            if (channel == null)
                throw new RuntimeException(
                        "Can't schedule future factoids in pm. "
                                + "Run this factoid at a channel.");
            this.delay = delay;
            this.channel = channel;
            this.arguments = arguments;
            this.sender = sender;
            this.key = key;
            this.quota = quota;
        }
        
        public void run()
        {
            try
            {
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
                String result = doFactImport(channel, arguments, sender, true,
                        quota);
                if (result.trim().equals(""))
                    return;
                if (result.startsWith("<ACTION>"))
                    bot.sendAction(channel, result.substring("<ACTION>"
                            .length()));
                else
                    bot.sendMessage(channel, result);
            }
        }
    }
    
    public static HashMap<String, FutureFactoid> futureFactoids = new HashMap<String, FutureFactoid>();
    public static final Object futureFactoidLock = new Object();
    public static boolean manualReconnect = false;
    public static final HashMap<String, Command> commands = new HashMap<String, Command>();
    public static final JZBot bot = new JZBot();
    // numeric 320: is signed on as account
    private static ProxyStorage<Storage> proxyStorage;
    public static Storage storage;
    
    public static Config config;
    public static boolean isRunning;
    
    public static void main(String[] args) throws Throwable
    {
        bot.setMessageDelay(250);
        bot.start();
    }
    
    private static void loadCommands()
    {
        loadCommand(new CommandListCommand());
        loadCommand(new ConfigCommand());
        loadCommand(new ExplainCommand());
        loadCommand(new FactoidCommand());
        loadCommand(new GoogleCommand());
        loadCommand(new HelpCommand());
        loadCommand(new IsRestrictedCommand());
        loadCommand(new JoinCommand());
        loadCommand(new JoinMessageCommand());
        loadCommand(new LeaveCommand());
        loadCommand(new LengthCommand());
        loadCommand(new MMCommand());
        loadCommand(new OpCommand());
        loadCommand(new ReconnectCommand());
        loadCommand(new RegexCommand());
        loadCommand(new RestrictCommand());
        // loadCommand(new RouletteCommand());
        loadCommand(new SayCommand());
        loadCommand(new ShutdownCommand());
        loadCommand(new SuperopCommand());
        loadCommand(new TriggerCommand());
        loadCommand(new TTTCommand());
        loadCommand(new UnrestrictCommand());
        loadCommand(new WeatherCommand());
    }
    
    private static void loadCommand(Command command)
    {
        commands.put(command.getName(), command);
    }
    
    private void start() throws Throwable
    {
        loadCommands();
        proxyStorage = new ProxyStorage<Storage>(Storage.class, new File(
                "storage/db"));
        storage = proxyStorage.getRoot();
        config = storage.getConfig();
        if (config == null)
        {
            config = storage.createConfig();
            storage.setConfig(config);
        }
        if (config.getNick() == null || config.getPassword() == null
                || config.getServer() == null)
        {
            System.out.println("No connect info specified. JZBot will exit.");
            System.exit(0);
        }
        bot.setLogin(config.getNick());
        bot.setName(config.getNick());
        try
        {
            if (config.getCharset() != null)
                bot.setEncoding(config.getCharset());
        }
        catch (UnsupportedEncodingException e)
        {
        }
        System.out.println("connecting");
        bot.connect(config.getServer(), config.getPort(), config.getPassword());
        System.out.println("connected");
    }
    
    protected void onJoin(String channel, String sender, String login,
            String hostname)
    {
        System.out.println("join detected on " + channel + " by " + sender);
        Channel chan = storage.getChannel(channel);
        if (chan == null)
            return;
        if (sender.equals(getName()))
        {
            Factoid f = chan.getFactoid("selfjoin");
            if (f != null)
            {
                incrementIndirectRequests(f);
                String factValue = safeRunFactoid(f, channel, sender,
                        new String[0], true, new HashMap<String, String>());
                if (factValue.trim().equals(""))
                    ;
                else if (factValue.startsWith("<ACTION>"))
                    sendAction(channel, factValue
                            .substring("<ACTION>".length()));
                else
                    sendMessage(channel, factValue);
            }
            return;
        }
        if (chan.getJoinFactoid() != null)
        {
            System.out.println("join factoid");
            String jfString = chan.getJoinFactoid();
            System.out.println("string " + jfString);
            if (jfString == null)
                jfString = "";
            String[] factoidList = jfString.split(" ");
            for (String fName : factoidList)
            {
                if (!fName.equals(""))
                {
                    System.out.println("issuing on join " + fName);
                    Factoid factoid = chan.getFactoid(fName);
                    if (factoid != null)
                    {
                        System.out.println("found, issuing");
                        sendMessage(channel, runFactoid(factoid, channel,
                                sender, new String[0],
                                new HashMap<String, String>(), true, null));
                    }
                }
            }
        }
    }
    
    public static void incrementIndirectRequests(Factoid f)
    {
        f.setIndirectRequests(f.getIndirectRequests() + 1);
    }
    
    public static void incrementDirectRequests(Factoid f)
    {
        System.out.println("Incrementing direct requests for " + f.getName());
        f.setDirectRequests(f.getDirectRequests() + 1);
        System.out.println("incremented");
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
    public static String runFactoid(Factoid factoid, String channel,
            String sender, String[] args, Map<String, String> vars,
            boolean allowRestricted, FactQuota quota)
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
        if (channel != null)
            vars.put("channel", channel);
        else
            vars.put("channel", "none");
        vars.put("self", bot.getNick());
        vars.put("source", channel == null ? sender : channel);
        String text = factoid.getValue();
        FactEntity parsedFactoid = FactParser.parse(text);
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
    
    public static String doFactImport(String channel, ArgumentList arguments,
            String sender, boolean allowRestricted, FactQuota quota)
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
            throw new RuntimeException("Invalid import factoid "
                    + arguments.get(0));
        incrementIndirectRequests(f);
        return runFactoid(f, channelSpecific ? channel : null, sender,
                arguments.subList(1).evalToArray(),
                new HashMap<String, String>(), allowRestricted, quota);
    }
    
    protected void onKick(String channel, String kickerNick,
            String kickerLogin, String kickerHostname, String recipientNick,
            String reason)
    {
        if (recipientNick.equals(this.getName()))
            joinChannel(channel);
    }
    
    private static String replaceVars(String text, Map<String, String> vars)
    {
        for (String key : vars.keySet())
        {
            String value = vars.get(key);
            text = text.replace("%" + key + "%", value);
        }
        text = text.replaceAll("\\%[^\\%]+%", "");
        return text;
    }
    
    protected void onMessage(String channel, String sender, String login,
            String hostname, String message)
    {
        System.out.println("Message from " + channel + " by " + sender + ": "
                + message);
        Channel chan = storage.getChannel(channel);
        if (chan == null)
        {
            System.out.println("No matching channel");
            return;
        }
        String trigger = chan.getTrigger();
        if (trigger != null && message.startsWith(trigger))
        {
            try
            {
                System.out.println("running message command");
                runMessageCommand(channel, false, sender, hostname, login,
                        message.substring(trigger.length()));
            }
            catch (Throwable e)
            {
                e.printStackTrace();
                JZBot.bot
                        .sendMessage(channel,
                                "Internal upper-propegation error: "
                                        + pastebinStack(e));
            }
        }
        else
        {
            System.out.println("Incorrect trigger");
        }
    }
    
    private void runMessageCommand(String channel, boolean pm, String sender,
            String hostname, String username, String message)
    {
        String[] commandSplit = message.split(" ", 2);
        String command = commandSplit[0];
        String commandArguments = (commandSplit.length == 1 ? ""
                : commandSplit[1]);
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
                    sendMessage(pm ? sender : channel, ((ResponseException) e)
                            .getMessage());
                }
                else
                {
                    e.printStackTrace();
                    sendMessage(pm ? sender : channel,
                            "An error occured while running the command "
                                    + command + ": " + pastebinStack(e));
                }
            }
            return;
        }
        /*
         * If we get here, then the text isn't a command. We'll check to see if
         * it's a factoid.
         * 
         * Our first check will be for a channel-specific factoid.
         */
        if (channel != null)
        {
            Channel cn = storage.getChannel(channel);
            if (cn != null)
            {
                Factoid f = cn.getFactoid(command);
                if (f != null)
                {
                    System.out.println("channel-specific factoid");
                    incrementDirectRequests(f);
                    String factValue;
                    factValue = safeRunFactoid(f, channel, sender,
                            commandArguments.split(" "), bot.isOp(channel,
                                    hostname), new HashMap<String, String>());
                    System.out.println("fact value: " + factValue);
                    if (factValue.trim().equals(""))
                        ;
                    else if (factValue.startsWith("<ACTION>"))
                        sendAction(channel, factValue.substring("<ACTION>"
                                .length()));
                    else
                        sendMessage(channel, factValue);
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
            incrementDirectRequests(f);
            System.out.println("requests incremented");
            String factValue;
            System.out.println("calculating fact value");
            factValue = safeRunFactoid(f, channel, sender, commandArguments
                    .split(" "), bot.isOp(channel, hostname),
                    new HashMap<String, String>());
            System.out.println("fact value: " + factValue);
            if (factValue.trim().equals(""))
                System.out.println("Empty value; doing nothing");
            else if (factValue.startsWith("<ACTION>"))
                sendAction(channel, factValue.substring("<ACTION>".length()));
            else
            {
                System.out.println("sending global message " + channel + " to "
                        + factValue);
                sendMessage(channel, factValue);
            }
            return;
        }
        System.out.println("invalid command");
        doInvalidCommand(pm, channel, sender);
    }
    
    /**
     * Runs the specified factoid, returning its output. If an exception is
     * thrown while running the factoid, the exception's stack trace, along with
     * some additional information, is sent to pastebin.com, and an error
     * message (including a url to the pastebin post) returned instead of the
     * factoid's output.
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
     *            The local variables to use. Extra variables will be added for
     *            function arguments and stuff like %channel%.
     * @return The output of the factoid, or an error message containing a
     *         pastebin url if the factoid threw an exception while running
     */
    public static String safeRunFactoid(Factoid f, String channel,
            String sender, String[] arguments, boolean allowRestricted,
            Map<String, String> vars)
    {
        String factValue;
        try
        {
            factValue = runFactoid(f, channel, sender, arguments, vars,
                    allowRestricted, null);
        }
        catch (Exception e)
        {
            factValue = "Syntax error while running factoid: "
                    + pastebinStack(e);
        }
        return factValue;
    }
    
    public static String pastebinStack(Throwable e)
    {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw, true));
        String eString = sw.toString();
        return "http://pastebin.com/"
                + Pastebin.createPost("jzbot", eString, Pastebin.Duration.DAY,
                        "");
    }
    
    private void doInvalidCommand(boolean pm, String channel, String sender)
    {
        if (channel != null)
            sendMessage(pm ? sender : channel,
                    "Huh? (pm \"help\" for more info)");
        else
            sendMessage(pm ? sender : channel,
                    "Huh? (pm \"help\" for more info)");
    }
    
    protected void onConnect()
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
                        joinChannel(channel.getName());
                    }
                }
            }
        }.start();
    }
    
    protected void onDisconnect()
    {
        System.out.println("on disconnect");
        proxyStorage.close();
        proxyStorage = new ProxyStorage<Storage>(Storage.class, new File(
                "storage/db"));
        storage = proxyStorage.getRoot();
        config = storage.getConfig();
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
                        reconnect();
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
    
    protected void onPrivateMessage(String sender, String login,
            String hostname, String message)
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
            runMessageCommand(channel, true, sender, hostname, login, message);
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            sendMessage(sender, "Internal upper-propegating pm exception: "
                    + pastebinStack(e));
        }
        
    }
    
    public static boolean isOp(String channel, String hostname)
    {
        if (isSuperop(hostname))
            return true;
        Channel c = storage.getChannel(channel);
        if (c == null)
            return false;
        return c.getOperator(hostname) != null;
    }
    
    public static boolean isSuperop(String hostname)
    {
        return storage.getOperator(hostname) != null;
    }
    
    public void verifyOp(String channel, String hostname)
    {
        if (!isOp(channel, hostname))
            throw new ResponseException(
                    "You are not an op, so you don't have permission to run this command.");
    }
    
    public void verifySuperop(String hostname)
    {
        if (!isSuperop(hostname))
            throw new ResponseException(
                    "You are not a superop, so you don't have permission to run this command.");
    }
    
    /**
     * Gets the nickname for the specified user. If the url is a nickname url,
     * then the nickname is simply taken from the url. If the url is an
     * authenticated url, then the corresponding protocol is asked for the
     * user's nickname.
     * 
     * @param user
     * @return
     */
    public static String getNickname(URI user)
    {
        return null;
    }
    
    /**
     * Gets the authenticated name for the specified user. If the user is an
     * authenticated url, then the authenticated name is simply taken from the
     * url. If the url is a nickname url, then the protocol is asked for the
     * user's authenticated name.<br/> <br/>
     * 
     * Due to the fact that nicknames can change frequently while authenticated
     * names generally won't, this should be called as soon after receiving a
     * nickname as possible, to avoid the wrong authenticated name being
     * obtained.<br/> <br/>
     * 
     * It's possible that the user's authenticated name and the user's nickname
     * are the same.
     * 
     * @param user
     * @return The user's authenticated name, or null if their protocol reports
     *         that the user is not authenticated
     */
    public static String getAuthname(URI user)
    {
        return null;
    }
    
    /**
     * Converts the specified nickname url to an authenticated name url, or null
     * if the user specified is not authenticated. If the url is already an
     * authenticated url, then it is returned as-is.
     * 
     * @param user
     * @return
     */
    public static URI toAuthForm(URI user)
    {
        return null;
    }
    
    public static User getUser(String channel, String sender)
    {
        User[] users = bot.getUsers(channel);
        for (User u : users)
        {
            if (sender.equalsIgnoreCase(u.getNick()))
                return u;
        }
        return null;
    }
    
    public static String evaluateEquation(String toEval, String channel)
    {
        return evaluateEquation(toEval, channel, config.getEvalEngine());
    }
    
    public static String evaluateEquation(String toEval, String channel,
            String engineName)
    {
        try
        {
            Evaluator engine = getEvalEngine(engineName);
            return engine.evaluate(toEval);
        }
        catch (Exception e)
        {
            throw new FactoidException("Exception while evaluating " + toEval
                    + " with engine " + config.getEvalEngine(), e);
        }
    }
    
    /**
     * Returns a string representing this double rounded to 8 decimal points,
     * and with no decimal point if one is not needed.
     * 
     * @param value
     *            The value to round
     * @return The value
     */
    static String toRoundedString(double value)
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
    
    public static void sendDelimited(String[] array, String delimiter,
            String recipient)
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
    
    public static String getCurrentCharset()
    {
        String charset = config.getCharset();
        if (charset == null)
            return Charset.defaultCharset().name();
        return config.getCharset();
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
        config.setCharset(charset);
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
    
    public static void reloadRegexes()
    {
        // TODO Auto-generated method stub
        
    }
}
