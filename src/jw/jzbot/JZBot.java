package jw.jzbot;

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
import java.math.BigInteger;
import java.math.MathContext;
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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.SystemMenuBar;

import jw.jzbot.commands.CommandListCommand;
import jw.jzbot.commands.ConfigCommand;
import jw.jzbot.commands.ExecCommand;
import jw.jzbot.commands.ExplainCommand;
import jw.jzbot.commands.FactoidCommand;
import jw.jzbot.commands.HelpCommand;
import jw.jzbot.commands.IsRestrictedCommand;
import jw.jzbot.commands.JoinCommand;
import jw.jzbot.commands.LeaveCommand;
import jw.jzbot.commands.MMCommand;
import jw.jzbot.commands.OpCommand;
import jw.jzbot.commands.ReconnectCommand;
import jw.jzbot.commands.RedefineCommand;
import jw.jzbot.commands.RegexCommand;
import jw.jzbot.commands.RestartCommand;
import jw.jzbot.commands.RestrictCommand;
import jw.jzbot.commands.ShutdownCommand;
import jw.jzbot.commands.StatusCommand;
import jw.jzbot.commands.SuperopCommand;
import jw.jzbot.commands.SwitchnickCommand;
import jw.jzbot.commands.TriggerCommand;
import jw.jzbot.commands.UnrestrictCommand;
import jw.jzbot.eval.CaltechEvaluator;
import jw.jzbot.eval.JEvalEvaluator;
import jw.jzbot.eval.JepliteEvaluator;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.FactEntity;
import jw.jzbot.fact.FactParser;
import jw.jzbot.fact.FactQuota;
import jw.jzbot.fact.FactoidException;
import jw.jzbot.fact.StringSink;
import jw.jzbot.help.DefaultHelpProvider;
import jw.jzbot.help.FunctionHelpProvider;
import jw.jzbot.help.PropsHelpProvider;
import jw.jzbot.help.XMLHelpProvider;
import jw.jzbot.pastebin.DefaultPastebinProviders;
import jw.jzbot.pastebin.PastebinProvider.Feature;
import jw.jzbot.protocols.BZFlagProtocol;
import jw.jzbot.protocols.FacebookProtocol;
import jw.jzbot.protocols.IrcProtocol;
import jw.jzbot.storage.*;
import jw.jzbot.utils.JZUtils;
import jw.jzbot.utils.Pastebin;

import net.sf.opengroove.common.proxystorage.ProxyStorage;
import net.sf.opengroove.common.proxystorage.StoredList;
import net.sf.opengroove.common.utils.StringUtils;
import net.sf.opengroove.common.utils.StringUtils.ToString;

import org.cheffo.jeplite.JEP;
import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;
import org.jibble.pircbot.PircBot;
import org.jibble.pircbot.User;

import sun.misc.Unsafe;

/**
 * jzbot authenticates off of hostmask.
 */
public class JZBot
{
    // public static Connection bot;
    public static Map<String, ConnectionContext> connectionMap = new HashMap<String, ConnectionContext>();
    
    public static File logsFolder = new File("storage/logs");
    
    public static Map<String, String> globalVariables = new HashMap<String, String>();
    
    public static ArrayList<HelpProvider> helpProviders = new ArrayList<HelpProvider>();
    
    public static Map<Integer, HttpServer> httpServers = new HashMap<Integer, HttpServer>();
    
    public static volatile int notificationSequence = 0;
    
    public static BlockingQueue<LogEvent> logQueue;
    
    public static final Object logQueueLock = new Object();
    
    private static final long CONNECTION_CYCLE_TIMEOUT = 60;
    
    public static int logQueueDelay;
    
    public static Thread notificationThread = new Thread("bot-internal-notification-thread")
    {
        public void run()
        {
            while (true)
            {
                try
                {
                    Thread.sleep(1000 * 60 * 5);
                    notificationSequence += 1;
                    notificationSequence %= 12;
                    sendNotificationToAll("fiveminutes");
                    if ((notificationSequence % 2) == 0)
                        sendNotificationToAll("tenminutes");
                    if ((notificationSequence % 6) == 0)
                        sendNotificationToAll("halfhour");
                    if ((notificationSequence % 12) == 0)
                        sendNotificationToAll("hour");
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    };
    static
    {
        notificationThread.setDaemon(true);
        notificationThread.start();
    }
    
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
    
    protected static void sendNotificationToAll(String name)
    {
        try
        {
            if (!bot.isConnected())
                return;
            for (String channel : bot.getChannels())
            {
                runNotificationFactoid(channel, null, bot.getNick(), "_on" + name,
                        new String[0], true);
            }
            runNotificationFactoid(null, null, bot.getNick(), "_on" + name, new String[0],
                    true);
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            try
            {
                bot.sendMessage(ConfigVars.primary.get(), "Global notification failure: "
                        + pastebinStack(e));
            }
            catch (Throwable e2)
            {
                e2.printStackTrace();
            }
        }
    }
    
    /**
     * Gets the connection object for the specified server, if it is currently connected.
     * If it is not currently connected, null is returned.
     * 
     * @param serverName
     * @return
     */
    public static ConnectionWrapper getConnection(String serverName)
    {
        ConnectionContext con = connectionMap.get(serverName);
        if (con == null)
            return null;
        if (!con.getConnection().isConnected())
            return null;
        return new ConnectionWrapper(con);
    }
    
    public static class ConnectionCycleThread extends Thread
    {
        public void run()
        {
            while (isRunning)
            {
                try
                {
                    doSingleConnectionCycle();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    // TODO: consider making these errors available to the bot's users?
                }
            }
        }
    }
    
    private static ConnectionCycleThread connectionCycleThread;
    
    private static BlockingQueue<Object> connectionCycleQueue = new LinkedBlockingQueue<Object>(
            3000);
    
    public static final Object connectionCycleLock = new Object();
    
    public static void startConnectionCycleThread()
    {
        connectionCycleThread = new ConnectionCycleThread();
        connectionCycleThread.setDaemon(true);
        connectionCycleThread.setPriority(3);
        connectionCycleThread.start();
    }
    
    public static void doSingleConnectionCycle() throws InterruptedException
    {
        connectionCycleQueue.poll(CONNECTION_CYCLE_TIMEOUT, TimeUnit.SECONDS);
        connectionCycleQueue.clear();
        /*
         * First step: create a connection object for all servers in the list
         */
        synchronized (connectionCycleLock)
        {
            for (Server server : storage.getServers().isolate())
            {
                if (connectionMap.get(server.getName()) == null)
                {
                    /*
                     * This server doesn't have a corresponding connection. Let's create
                     * one for it.
                     */
                    ConnectionContext context = new ConnectionContext();
                    context.setServerName(server.getName());
                    context.setDatastoreServer(server);
                    Connection c = instantiateConnectionForProtocol(server.getProtocol(),
                            true);
                    context.setConnection(c);
                    c.init(context);
                    connectionMap.put(server.getName(), context);
                }
            }
        }
        /*
         * Second step: iterate through all connection objects and check to see if they
         * are disconnected. If they are, check to see if their database server object is
         * active. If it is, attempt to connect the connection.
         */
        for (ConnectionContext context : connectionMap.values())
        {
            if (!context.getConnection().isConnected())
            {
                if (context.getDatastoreServer().isActive())
                {
                    try
                    {
                        context.getConnection().connect();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                        // FIXME: log this so that the jzbot user can see that the
                        // connection
                        // failed, and allow them to see why. Maybe add a global
                        // notification
                        // for when a server fails to connect (or maybe a server
                        // notification;
                        // figure out how that would work with disconnected servers
                        // running
                        // factoids etc)
                    }
                }
            }
        }
        /*
         * Third step: disconnect all connections if they are connected but their server
         * object is inactive or their server object is no longer present in the server
         * objects list
         */
        synchronized (connectionCycleLock)
        {
            for (ConnectionContext context : connectionMap.values())
            {
                if (context.getConnection().isConnected())
                {
                    if ((!context.getDatastoreServer().isActive())
                            || !storage.getServers().contains(context.getDatastoreServer()))
                    // if the server is not active or the server is no longer in the list
                    {
                        context.getConnection().disconnect(getDefaultDisconnectMessage());
                    }
                }
            }
        }
        /*
         * Fourth step: find all connection objects whose server objects are not in the
         * list anymore and delete them.
         */
        synchronized (connectionCycleLock)
        {
            for (ConnectionContext context : connectionMap.values())
            {
                if (!storage.getServers().contains(context.getDatastoreServer()))
                {
                    context.getConnection().discard();
                    connectionMap.remove(context.getDatastoreServer().getName());
                }
            }
        }
        /*
         * ...and we're done!
         */
    }
    
    private static String getDefaultDisconnectMessage()
    {
        return "Laters.";
    }
    
    public static Connection instantiateConnectionForProtocol(String name, boolean run)
    {
        /*
         * TODO: the protocol list is hard-coded right now; this should ideally be split
         * into a .props file; consider jw/jzbot/protocols.props.
         */
        Class<? extends Connection> c;
        if (name.equals("irc"))
            c = IrcProtocol.class;
        else if (name.equals("bzflag"))
            c = BZFlagProtocol.class;
        else if (name.equals("facebook"))
            c = FacebookProtocol.class;
        else
            throw new RuntimeException("The protocol \"" + name
                    + "\" is not a valid protocol name. Valid protocol names are, "
                    + "at present, \"irc\", \"bzflag\", and \"facebook\".");
        if (!run)
            return null;
        try
        {
            return c.newInstance();
        }
        catch (Exception e)
        {
            /*
             * We need to catch Exception instead of just the two Exception types
             * newInstance throws as newInstance propegates checked exceptions too, which
             * isn't good
             */
            throw new RuntimeException("Exception while instantiating protocol \"" + name
                    + "\" for class " + c.getName(), e);
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
    
    public static class FutureFactoid implements Runnable
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
            if (delay > (86400 * 2))
                throw new RuntimeException("Futures can't be scheduled more than 2 days ("
                        + (86400 * 2) + " seconds) into the future. You're "
                        + "trying to schedule " + "a future to run sooner than that.");
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
            /*
             * We want to force the arguments to be evaluated now, instead of when the
             * future is run
             */
            for (int i = 0; i < arguments.length(); i++)
                arguments.getString(i);
        }
        
        public void run()
        {
            // try
            // {
            // startTime = System.currentTimeMillis() + (delay * 1000);
            // Thread.sleep(delay * 1000);
            // }
            // catch (InterruptedException e)
            // {
            // }
            synchronized (futureFactoidLock)
            {
                if (futureFactoids.get(key) != this)
                    return;
                futureFactoids.remove(key);
                TimedKillThread tkt = new TimedKillThread(Thread.currentThread());
                tkt.start();
                String result;
                try
                {
                    result = doFactImport(channel, arguments, sender, true, quota,
                            ImportLevel.any);
                }
                catch (Throwable t)
                {
                    result = "A future error occured: " + pastebinStack(t);
                }
                finally
                {
                    tkt.active = false;
                }
                if (result.trim().equals(""))
                    return;
                if (result.startsWith("<ACTION>"))
                    bot.sendAction(channel, result.substring("<ACTION>".length()));
                else
                    bot.sendMessage(channel, result);
            }
        }
        
        public void start()
        {
            futureFactoidPool.schedule(this, delay, TimeUnit.SECONDS);
        }
    }
    
    public static HashMap<String, FutureFactoid> futureFactoids = new HashMap<String, FutureFactoid>();
    public static final Object futureFactoidLock = new Object();
    public static ScheduledThreadPoolExecutor futureFactoidPool = new ScheduledThreadPoolExecutor(
            1);
    public static boolean manualReconnect = false;
    public static final HashMap<String, Command> commands = new HashMap<String, Command>();
    // numeric 320: is signed on as account
    public static ProxyStorage<Storage> proxyStorage;
    public static Storage storage;
    
    public static Config config;
    public static boolean isRunning = true;
    
    public static volatile boolean logQueueRunning = true;
    
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
                System.out.println("Waiting on log queue to shut down...");
                synchronized (logQueueLock)
                {
                    logQueueRunning = false;
                    int discarded = logQueue.size();
                    System.out.println("Log queue has shut down. " + discarded
                            + " log event" + (discarded == 1 ? "" : "s")
                            + " were discarded.");
                }
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
            System.out.println("");
            System.out.println("For advanced users, you can configure a protocol ");
            System.out.println("implementation to use by running \"jzbot protocol ");
            System.out.println("<class>\", where <class> is the name of a class ");
            System.out.println("that implements jw.jzbot.Protocol.");
            System.out.println("Most users will not need to do this.");
        }
        else if (args[0].equals("protocol"))
        {
            initProxyStorage();
            if (config == null)
            {
                System.out.println("You need to run \"jzbot protocol\" *after* you");
                System.out.println("run \"jzbot setup\".");
                return;
            }
            if (args.length <= 1)
            {
                System.out.println("You need to specify the protocol to use. Try ");
                System.out.println("\"jzbot protocol <class>\", or \"jzbot help\".");
                return;
            }
            config.setProtocol(args[1]);
            System.out.println("The protocol has been successfully set to \""
                    + config.getProtocol() + "\".");
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
        else if (args[0].equals("config"))
        {
            initProxyStorage();
            if (args.length == 1)
            {
                System.out.println("All config var names: "
                        + StringUtils.delimited(ConfigVars.values(),
                                new ToString<ConfigVars>()
                                {
                                    
                                    @Override
                                    public String toString(ConfigVars object)
                                    {
                                        return object.name();
                                    }
                                }, ", "));
            }
            else if (args.length == 2)
            {
                System.out.println("Config variable \"" + args[1]
                        + "\" is currently set to \"" + ConfigVars.valueOf(args[1]).get());
            }
            else
            {
                ConfigVars.valueOf(args[1]).set(args[2]);
                System.out.println("Successfully set the var \"" + args[1]
                        + "\" to the value \"" + args[2] + "\".");
            }
        }
        else if (args[0].equals("switchnick"))
        {
            if (args.length > 1)
            {
                initProxyStorage();
                config.setNick(args[1]);
                System.out.println("Successfully set the bot's nick to be \"" + args[1]
                        + "\".");
            }
            else
            {
                System.out.println("You need to specify a new nickname for the "
                        + "bot to use. Try \"jzbot switchnick <newnick>\".");
            }
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
        loadCommand(new RestartCommand());
        loadCommand(new RestrictCommand());
        // loadCommand(new RouletteCommand());
        // loadCommand(new SayCommand());
        loadCommand(new ShutdownCommand());
        loadCommand(new StatusCommand());
        loadCommand(new SuperopCommand());
        loadCommand(new SwitchnickCommand());
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
        DefaultPastebinProviders.installDefaultSet();
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
        startLogSinkThread();
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
        logQueue = new LinkedBlockingQueue<LogEvent>(Integer.parseInt(ConfigVars.lqmaxsize
                .get()));
        logQueueDelay = Integer.parseInt(ConfigVars.lqdelay.get());
    }
    
    @SuppressWarnings("unchecked")
    private static void loadProtocol()
    {
        String protocolClass = config.getProtocol();
        System.out.println("Using protocol " + protocolClass);
        try
        {
            Class<? extends Connection> classObject = (Class<? extends Connection>) Class
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
    
    public static HashMap<String, String> channelTopics = new HashMap<String, String>();
    
    public static void onTopic(String channel, String topic, String setBy, long date,
            boolean changed)
    {
        channelTopics.put(channel, topic);
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
    
    /**
     * Runs the specified notification factoid.
     * 
     * @param channelName
     *            The channel the event occurred at. This must either be the name of a
     *            channel that the bot is currently at, or null. If it's null, then this
     *            means this "event" is a global event, and the notification factoids will
     *            be run at all channels (and if there are any global notification
     *            factoids for this event, they will be run too).
     * @param chan
     *            The channel object. Normally this is null. Since obtaining a Channel
     *            object from the database is somewhat expensive, then if the code that
     *            calls this method already has a Channel object it should generally pass
     *            it in here. This channel's name must be the same as <tt>channelName</tt>
     *            , and this must be null if <tt>channelName</tt> is null.
     * @param sender
     *            The person that triggered the event. When the notification factoids are
     *            run, %0% and %who% will be set to the value of this parameter.
     * @param factname
     *            The prefix of the notification factoid to run. All factoids whose names
     *            are either equal to this name or whose names start with this name plus
     *            an underscore will be run. For example, if <tt>factname</tt> is "_test",
     *            then the factoids "_test", "_test_something", and "_test_other" would be
     *            run, but not the factoid "_testsomething".
     * @param args
     *            The arguments to pass to the factoids when they are run. The first
     *            argument will be put into the local varaible %1%, the second argument
     *            will be put into %2%, and so on. This can be either null or an array
     *            with no items in it to indicate that there should be no arguments.
     * @param timed
     *            True if this is timed, false if it is not. This should almost always be
     *            true. If this is true, then a TimedKillThread will be started that will
     *            forcibly terminate the factoids if they run for more than 60 seconds. If
     *            this is false, the factoids will never be forcibly terminated, which
     *            could freeze the bot if the factoids have an infinte loop or some such
     *            other problem.
     */
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
        String factoidName = factoid.getName();
        long startMillis = System.currentTimeMillis();
        FactEntity parsedFactoid = FactParser.parse(text, factoid.getName());
        long parsedMillis = System.currentTimeMillis();
        FactContext context = new FactContext();
        context.setQuota(quota);
        context.setChannel(channel);
        context.setSender(sender);
        context.setGlobalVars(globalVariables);
        context.setLocalVars(vars);
        context.setSelf(bot.getNick());
        // Now we actually run the factoid.
        StringSink resultSink = new StringSink();
        parsedFactoid.resolve(resultSink, context);
        String result = resultSink.toString();
        long finishedMillis = System.currentTimeMillis();
        System.out.println(factoidName + ": Parsed in " + (parsedMillis - startMillis)
                + " ms, ran in " + (finishedMillis - parsedMillis) + " ms");
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
            Channel cn = JZBot.storage.getChannel(channel);
            String factname = arguments.getString(0);
            if (cn != null)
                f = cn.getFactoid(factname);
            if (f != null)
                channelSpecific = true;
        }
        if (f == null)
        {
            f = JZBot.storage.getFactoid(arguments.getString(0));
        }
        if (f == null)
            throw new RuntimeException("Invalid import factoid " + arguments.getString(0));
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
    
    public static void onNotice(String sourceNick, String sourceLogin,
            String sourceHostname, String target, String line)
    {
        logEvent(target, "notice", sourceNick, line);
        String[] arguments =
        {
            line
        }; // Send the text as parameter %1%
        if (!(target.equals(bot.getNick())) || !(sourceNick.equals(bot.getNick()))) // if
        // the
        // target
        // of
        // the
        // notice
        // is
        // the
        // bots
        // nick
        // it is a Global Notice
        {
            runNotificationFactoid(target, null, sourceNick, "_onnotice", arguments, true);
        }
        if (target.equals(bot.getNick())) // if the target of the notice is the bots nick
        // it is a Personal Notice
        {
            String[] channels = JZBot.bot.getChannels(); // get a list of channels where
            // the bot is in
            for (String channel : channels)
            { // for each channel it activates _onPRIVNotice
                runNotificationFactoid(channel, null, sourceNick, "_onprivnotice",
                        arguments, true);
            }
        }
        System.out.println("Notice to " + target + " by " + sourceNick + ": " + line);
        
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
        System.out.println("Starting command run for message " + message);
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
            System.out.println("Finishing command run #1");
            return;
        }
        /*
         * If we get here, then the text isn't a command. We'll check to see if it's a
         * factoid.
         * 
         * Our first check will be for a channel-specific factoid.
         */
        if (!processFactoids)
        {
            System.out.println("Finishing command run #2");
            return;
        }
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
                        System.out.println("Finishing command run #3");
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
                    System.out.println("Finishing command run #4");
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
                System.out.println("Finishing command run #5");
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
            System.out.println("Finishing command run #6");
            return;
        }
        System.out.println("invalid command");
        doInvalidCommand(pm, channel, sender);
        System.out.println("Finishing command run #7");
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
            return pastebinNotice(eString, null);
        }
        catch (Exception e2)
        {
            e2.printStackTrace();
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
                if (ConfigVars.servicemsg.get().equals("1"))
                {
                    System.out.println("Authenticating with NickServ via privmsg");
                    String pwd = config.getPassword();
                    if (pwd != null && !"".equals(pwd))
                        bot.sendMessage("NickServ", "identify " + pwd);
                }
                try
                {
                    Thread.sleep(2500);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
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
                sendNotificationToAll("ready");
                try
                {
                    Thread.sleep(3500);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                String umodes = ConfigVars.modes.get();
                for (char c : umodes.toCharArray())
                {
                    bot.setMode(bot.getNick(), "+" + c);
                }
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
            channelTopics.clear();
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
        System.out.println("starting reconnect thread");
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
    
    public static boolean isSuperop(String serverName, String hostname)
    {
        if (elevatedSuperopList.contains(serverName + "@" + hostname))
            return true;
        Server server = storage.getServer(serverName);
        if (server == null)
            return false;
        return server.getOperator(hostname) != null;
    }
    
    public static void verifySuperop(String server, String hostname)
    {
        if (!isSuperop(server, hostname))
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
        LogEvent e = new LogEvent();
        e.channel = channel;
        e.event = event;
        e.nick = nick;
        e.details = details;
        synchronized (logQueueLock)
        {
            logQueue.offer(e);
        }
    }
    
    public static void startLogSinkThread()
    {
        Thread thread = new Thread()
        {
            public void run()
            {
                while (isRunning && logQueueRunning)
                {
                    try
                    {
                        Thread.sleep(logQueueDelay);
                        System.out.println("Sinking log events...");
                        int events = 0;
                        LogEvent event;
                        synchronized (logQueueLock)
                        {
                            if (logQueueRunning)
                            {
                                while ((event = logQueue.poll()) != null)
                                {
                                    events++;
                                    sinkQueuedLogEvent(event);
                                }
                            }
                        }
                        System.out.println("Sunk " + events + " events.");
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        };
        thread.start();
    }
    
    public static void sinkQueuedLogEvent(LogEvent logEvent)
    {
        String channel = logEvent.channel;
        String nick = logEvent.nick;
        String details = logEvent.details;
        String event = logEvent.event;
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
    
    public static String pastebinNotice(String text, Feature[] features)
    {
        return Pastebin.createPost("jzbot", text
                + "\n\n\n\n\nPASTEBIN OWNER: If you have questions about this "
                + "post, or its creator (JZBot), send an email to \"alex"
                + " AT opengroove DOT org\".\n\n", Pastebin.Duration.DAY, null, features);
    }
    
    public static MathContext datasizeContext = new MathContext(3);
    
    public static BigDecimal kilo = new BigDecimal("1024");
    
    public static BigDecimal one = new BigDecimal("1");
    
    /**
     * Formats the specified data size, in bytes, to be suffixed with MB, GB, KB, etc.
     * 
     * @param size
     * @return
     */
    public static String datasize(long size)
    {
        BigDecimal number = new BigDecimal("" + size);
        String suffix = "B";
        if (number.divide(kilo).compareTo(one) > 0)
        {
            suffix = "KB";
            number = number.divide(kilo);
            if (number.divide(kilo).compareTo(one) > 0)
            {
                suffix = "MB";
                number = number.divide(kilo);
                if (number.divide(kilo).compareTo(one) > 0)
                {
                    suffix = "GB";
                    number = number.divide(kilo);
                }
            }
        }
        number = number.round(datasizeContext);
        return "" + number + suffix;
    }
    
    public static boolean isValidFactoidName(String factoidName)
    {
        return factoidName.matches("^[^\\@\\#\\%].*$");
    }
}
