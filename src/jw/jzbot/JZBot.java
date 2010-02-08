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
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
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
import jw.jzbot.commands.JoinCommand;
import jw.jzbot.commands.LeaveCommand;
import jw.jzbot.commands.ListChannelsCommand;
import jw.jzbot.commands.MMCommand;
import jw.jzbot.commands.RedefineCommand;
import jw.jzbot.commands.RegexCommand;
import jw.jzbot.commands.RestartCommand;
import jw.jzbot.commands.ServerCommand;
import jw.jzbot.commands.ShutdownCommand;
import jw.jzbot.commands.StatusCommand;
import jw.jzbot.commands.SuperopCommand;
import jw.jzbot.commands.SwitchnickCommand;
import jw.jzbot.commands.TriggerCommand;
import jw.jzbot.eval.CaltechEvaluator;
import jw.jzbot.eval.JEvalEvaluator;
import jw.jzbot.eval.JepliteEvaluator;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.FactParser;
import jw.jzbot.fact.FactQuota;
import jw.jzbot.fact.ast.FactEntity;
import jw.jzbot.fact.exceptions.FactoidException;
import jw.jzbot.fact.output.NullSink;
import jw.jzbot.fact.output.StringSink;
import jw.jzbot.help.DefaultHelpProvider;
import jw.jzbot.help.FunctionHelpProvider;
import jw.jzbot.help.PropsHelpProvider;
import jw.jzbot.help.XMLHelpProvider;
import jw.jzbot.pastebin.DefaultPastebinProviders;
import jw.jzbot.pastebin.PastebinProvider.Feature;
import jw.jzbot.protocols.BZFlagProtocol;
import jw.jzbot.protocols.FacebookProtocol;
import jw.jzbot.protocols.IrcProtocol;
import jw.jzbot.protocols.xmpp.XmppProtocol;
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
    public static Map<String, ConnectionContext> connectionMap =
            new HashMap<String, ConnectionContext>();
    
    public static File logsFolder = new File("storage/logs");
    
    public static Map<String, String> globalVariables = new HashMap<String, String>();
    
    public static ArrayList<HelpProvider> helpProviders = new ArrayList<HelpProvider>();
    
    public static Map<Integer, HttpServer> httpServers = new HashMap<Integer, HttpServer>();
    
    public static volatile int notificationSequence = 0;
    
    public static BlockingQueue<LogEvent> logQueue;
    
    public static final Object logQueueLock = new Object();
    // TODO: consider changing this to 90
    private static final long CONNECTION_CYCLE_TIMEOUT = 120;
    
    public static int logQueueDelay;
    
    public static Thread notificationThread =
            new Thread("bot-internal-notification-thread")
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
    
    /**
     * Runs the specified notification first globally, then on all connected servers
     * (Read: not servers that are disconnected, deactivated, or no longer existent), then
     * on all channels within those servers.
     * 
     * @param name
     */
    protected static void sendNotificationToAll(String name)
    {
        try
        {
            runNotificationFactoid(null, null, null, null, "", "_on" + name, new String[0],
                    true, false);
            synchronized (connectionCycleLock)
            {
                for (ConnectionContext context : connectionMap.values())
                {
                    if (!context.getConnection().isConnected())
                        return;
                    runNotificationFactoid(context.getServerName(), context
                            .getDatastoreServer(), null, null, context.getConnection()
                            .getNick(), "_on" + name, new String[0], true, false);
                    for (String channel : context.getConnection().getChannels())
                    {
                        runNotificationFactoid(context.getServerName(), context
                                .getDatastoreServer(), channel, null, context
                                .getConnection().getNick(), "_on" + name, new String[0],
                                true, false);
                    }
                }
            }
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            try
            {
                sendMessageToTarget(ConfigVars.primary.get(),
                        "Global notification failure: " + pastebinStack(e));
            }
            catch (Throwable e2)
            {
                e2.printStackTrace();
            }
        }
    }
    
    /**
     * Sends a message to the specified target, which is a canonical channel name. If the
     * specified server is not connected or if the specified channel is not joined, this
     * message is silently discarded.<br/><br/>
     * 
     * This <b>must not</b> be called from any logging code, as this method will invoke
     * the channel logger to log that a message was sent.
     * 
     * @param target
     *            The target to send to
     * @param message
     *            The message to send
     */
    public static void sendMessageToTarget(String target, String message)
    {
        String serverName;
        String channelName;
        try
        {
            serverName = extractServerName(target);
            channelName = extractChannelName(target);
        }
        catch (Exception e)
        {
            /*
             * This will happen if the target is malformed. We'll just ignore it for now.
             */
            e.printStackTrace();
            return;
        }
        ConnectionWrapper wrapper = getConnection(serverName);
        if (wrapper == null)
            return;
        ConnectionContext context = wrapper.getContext();
        Connection connection = context.getConnection();
        if (!connection.isConnected())
            return;
        wrapper.sendMessage(channelName, message);
    }
    
    public static String extractChannelName(String target)
    {
        if (target.startsWith("#"))
            return target;
        if (target.startsWith("@") && target.contains("#"))
            return target.substring(target.indexOf('#'));
        throw new IllegalArgumentException("The target \"" + target
            + "\" does not contain a valid channel.");
    }
    
    public static String extractServerName(String target)
    {
        if (target.startsWith("@") && target.contains("#"))
            return target.substring(1, target.indexOf('#'));
        if (target.startsWith("@"))
            return target.substring(1);
        throw new IllegalArgumentException("The target \"" + target
            + "\" does not contain a valid server name.");
    }
    
    public static String extractRelativeServer(String target, Scope scope)
    {
        if (target.startsWith("@") && target.contains("#"))
            return target.substring(1, target.indexOf('#'));
        if (target.startsWith("@"))
            return target.substring(1);
        if (scope != null)
            return scope.getServerName();
        return null;
    }
    
    public static String extractRelativeChannel(String target, Scope scope)
    {
        if (target.startsWith("#"))
            return target;
        if (target.startsWith("@") && target.contains("#"))
            return target.substring(target.indexOf('#'));
        if (scope != null && !target.startsWith("@"))
            /*
             * The check for "@" is included so that we don't get the channel we're scoped
             * to on an entirely different server.
             */
            return scope.getChannel();
        return null;
    }
    
    /**
     * Gets the connection object for the specified server, if it iss/difficult currently
     * connected. If it is not currently connected, null is returned.
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
        // FIXME: add caching of the connection wrapper
        return new ConnectionWrapper(con);
    }
    
    /**
     * Extracts the server name from the target specified (which could be
     * "@server#channel", for example), then gets and returns the connection for that
     * server. If the target does not specify a server, then the connection for the server
     * <tt>scope</tt> is currently using is returned instead. If <tt>scope</tt> doesn't
     * contain a server and <tt>target</tt> doesn't contain one either, an exception is
     * thrown.
     * 
     * @param target
     * @param scope
     * @return
     */
    public static ConnectionWrapper checkedGetExtractedConnection(String target, Scope scope)
    {
        String serverName = extractRelativeServer(target, scope);
        if (serverName == null)
            throw new FactoidException("The target \"" + target
                + "\" was expected to contain a server name, but it "
                + "did not and the current scope doesn't contain "
                + "a server name either. Consider wrapping this "
                + "function call with a call to the {scope} "
                + "function to add a server to the current scope, "
                + "or just specify a server in the target string " + "that you're using.");
        ConnectionWrapper con = getConnection(serverName);
        if (con == null)
            throw new FactoidException("There isn't a connection for the server name "
                + serverName
                + ". This probably means that the server is currently disconnected.");
        return con;
    }
    
    public static ConnectionContext getRealConnection(String serverName)
    {
        ConnectionContext con = connectionMap.get(serverName);
        if (con == null)
            return null;
        if (!con.getConnection().isConnected())
            return null;
        return con;
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
    
    private static BlockingQueue<Object> connectionCycleQueue =
            new LinkedBlockingQueue<Object>(500);
    
    public static Map<String, Throwable> connectionLastErrorMap =
            new HashMap<String, Throwable>();
    
    public static final Object connectionCycleLock = new Object();
    
    public static void startConnectionCycleThread()
    {
        connectionCycleThread = new ConnectionCycleThread();
        connectionCycleThread.setDaemon(true);
        connectionCycleThread.setPriority(3);
        connectionCycleThread.start();
    }
    
    /**
     * This is called about once every 2 minutes. It performs the main connection cycle.
     * It essentially connects the bot to servers that it's supposed to be connected to,
     * and disconnects the bot from servers that it's not supposed to be connected to.
     * 
     * @throws InterruptedException
     */
    public static void doSingleConnectionCycle() throws InterruptedException
    {
        connectionCycleQueue.poll(CONNECTION_CYCLE_TIMEOUT, TimeUnit.SECONDS);
        connectionCycleQueue.clear();
        // We'll wait a bit, mostly for the heck of it
        Thread.sleep(2000);
        System.out.println("Connection cycle");
        /*
         * First step: create a connection object for all servers in the list
         */
        synchronized (connectionCycleLock)
        {
            System.out.println("Synchronized on connection cycle");
            for (Server server : storage.getServers().isolate())
            {
                String serverName = server.getName();
                if (connectionMap.get(serverName) == null)
                {
                    /*
                     * This server doesn't have a corresponding connection. Let's create
                     * one for it.
                     */
                    System.out.println("Building connection for server " + serverName
                        + "...");
                    ConnectionContext context = new ConnectionContext();
                    context.setServerName(serverName);
                    context.setDatastoreServer(server);
                    System.out.println("Instantiating protocol instance...");
                    Connection c =
                            instantiateConnectionForProtocol(server.getProtocol(), true);
                    context.setConnection(c);
                    System.out.println("Initializing protocol...");
                    c.init(context);
                    System.out.println("Registering connection...");
                    connectionMap.put(server.getName(), context);
                    System.out.println("Connection built successfully.");
                }
            }
        }
        /*
         * Second step: iterate through all connection objects and check to see if they
         * are disconnected. If they are, check to see if their database server object is
         * active. If it is, attempt to connect the connection. However, if the connection
         * has been connected at least once before (as determined by the connection
         * context), then the connection should be discarded and the connection cycle
         * thread notified. This will cause this whole method to run again, and that time
         * through it will create a new connection and connect it. This ensures that a
         * given connection object is never re-used.
         */
        for (ConnectionContext context : connectionMap.values())
        {
            if (!context.getConnection().isConnected())
            {
                if (context.getDatastoreServer().isActive())
                {
                    if (context.hasConnected())
                    {
                        context.markDiscardNeeded();
                        notifyConnectionCycleThread();
                    }
                    else
                    {
                        try
                        {
                            System.out.println("Running pre-connect actions for server "
                                + context.getServerName());
                            runPreConnectActions(context);
                            System.out.println("Connecting to server "
                                + context.getServerName());
                            context.getConnection().connect();
                            context.markConnected();
                            System.out.println("Connection established.");
                            connectionLastErrorMap.remove(context.getServerName());
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                            connectionLastErrorMap.put(context.getServerName(), e);
                        }
                    }
                }
            }
        }
        /*
         * Third step: disconnect all connections if they are connected but their server
         * object is inactive, their server object is no longer present in the server
         * objects list, or the connection has been marked as needing to be discarded
         */
        synchronized (connectionCycleLock)
        {
            for (ConnectionContext context : connectionMap.values())
            {
                if (context.getConnection().isConnected())
                {
                    if ((!context.getDatastoreServer().isActive())
                        || !storage.getServers().contains(context.getDatastoreServer())
                        || context.discardNeeded())
                    // if the server is not active or the server is no longer in the list
                    {
                        System.out.println("Disconnecting from server "
                            + context.getServerName());
                        context.getConnection().disconnect(getDefaultDisconnectMessage());
                        System.out.println("Disconnected.");
                    }
                }
            }
        }
        /*
         * Fourth step: find all connection objects whose server objects are not in the
         * list anymore, or that are marked as needing to be discarded, and delete them.
         */
        synchronized (connectionCycleLock)
        {
            for (ConnectionContext context : new ArrayList<ConnectionContext>(connectionMap
                    .values()))
            {
                if ((!storage.getServers().contains(context.getDatastoreServer()))
                    || context.discardNeeded())
                {
                    System.out.println("Discarding connection for server "
                        + context.getServerName());
                    context.getConnection().discard();
                    System.out.println("Unregistering connection...");
                    connectionMap.remove(context.getDatastoreServer().getName());
                    System.out.println("Connection discarded successfully.");
                }
            }
        }
        /*
         * ...and we're done!
         */
    }
    
    public static void notifyConnectionCycleThread()
    {
        connectionCycleQueue.offer(new Object());
    }
    
    private static void runPreConnectActions(ConnectionContext context)
    {
        context.getConnection().setMessageDelay(Integer.parseInt(ConfigVars.delay.get()));
        // TODO: might want to change this to an onVersion method in IrcProtocol itself,
        // and have it retrievable from the context
        context.getConnection().setVersion("JZBot -- http://jzbot.googlecode.com");
        try
        {
            context.getConnection().setEncoding(ConfigVars.charset.get());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    private static String getDefaultDisconnectMessage()
    {
        return "Laters.";
    }
    
    public static Connection instantiateConnectionForProtocol(String name, boolean run)
    {
        /*
         * TODO: the protocol list is hard-coded right now; this should ideally be split
         * into a .props file; consider jw/jzbot/protocols/protocols.props.
         */
        Class<? extends Connection> c;
        if (name.equals("irc"))
            c = IrcProtocol.class;
        else if (name.equals("bzflag"))
            c = BZFlagProtocol.class;
        else if (name.equals("facebook"))
            c = FacebookProtocol.class;
        else if (name.equals("xmpp"))
            c = XmppProtocol.class;
        else
            throw new ResponseException("The protocol \"" + name
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
             * isn't good.
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
                    "Exception occurred while stopping an http server on port " + port);
        }
    }
    
    public static final File serverPortsFile = new File("storage", "serverports.txt");
    public static final File maxServersFile = new File("storage", "maxservers.txt");
    
    /**
     * Throws an exception if a server cannot be started on the specified port. The
     * reasons for throwing an exception are, at present:<br/>
     * <ul>
     * <li>HTTP servers are disabled because storage/serverports.txt does not exist</li>
     * <li>The specified port does not match the regex in storage/serverports.txt</li>
     * <li>There are too many servers already started</li>
     * </ul>
     * 
     * @param port
     */
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
            throw new RuntimeException("Invalid evaluator engine name: " + name
                + ", expected one of "
                + StringUtils.delimited(evalEngines.keySet().toArray(new String[0]), ", "));
        return engine;
    }
    
    public static Evaluator getDefaultEvalEngine(String channel)
    {
        return getEvalEngine(ConfigVars.evalengine.get());
    }
    
    public static class FutureFactoid implements Runnable
    {
        private int delay;
        private String server;
        private String channel;
        private ArgumentList arguments;
        private ServerUser sender;
        private Messenger source;
        private String key;
        private FactQuota quota;
        public long startTime;
        
        public FutureFactoid(int delay, String server, String channel,
                ArgumentList arguments, ServerUser sender, Messenger source, String key,
                FactQuota quota)
        {
            if (delay > (86400 * 2))
                throw new RuntimeException("Futures can't be scheduled more than 2 days ("
                    + (86400 * 2) + " seconds) into the future. You're "
                    + "trying to schedule " + "a future to run sooner than that.");
            // FIXME: This needs to be changed so that future factoids can be scheduled in
            // factoids that are not channel-scoped.
            if (channel == null)
                throw new RuntimeException("Can't schedule future factoids in pm. "
                    + "Run this factoid at a channel.");
            this.delay = delay;
            this.server = server;
            this.channel = channel;
            this.arguments = arguments;
            this.sender = sender;
            this.source = source;
            this.key = key;
            this.quota = quota;
            startTime = System.currentTimeMillis() + (delay * 1000);
            /*
             * We want to force the arguments to be evaluated now, instead of when the
             * future is run
             */
            for (int i = 0; i < arguments.length(); i++)
                arguments.get(i, new NullSink());
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
                    result =
                            doFactImport(server, channel, arguments, sender, source, true,
                                    quota, ImportLevel.any);
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
                // TODO: this doesn't properly catch the case where the server is
                // nonexistent or the server is not connected. Add code to deal with these
                // two cases.
                sendActionOrMessage(getConnection(server), channel, result);
            }
        }
        
        public void start()
        {
            futureFactoidPool.schedule(this, delay, TimeUnit.SECONDS);
        }
    }
    
    public static HashMap<String, FutureFactoid> futureFactoids =
            new HashMap<String, FutureFactoid>();
    public static final Object futureFactoidLock = new Object();
    public static ScheduledThreadPoolExecutor futureFactoidPool =
            new ScheduledThreadPoolExecutor(1);
    public static final HashMap<String, Command> commands = new HashMap<String, Command>();
    // numeric 320: is signed on as account
    public static ProxyStorage<Storage> proxyStorage;
    public static Storage storage;
    
    public static Config config;
    public static boolean isRunning = true;
    
    public static volatile boolean logQueueRunning = true;
    
    public static void main(String[] args) throws Throwable
    {
        System.out.println("This is JZBot, http://jzbot.googlecode.com");
        // System.out.println("Revision " + VersionInfo.revision + ", built on "
        // + VersionInfo.shortDateString);
        System.out.println("Written by Alexander Boyd, Maximilian Dirkmann, "
            + "and Jonathon Kloster");
        System.out.println("A.K.A.s, respectively, javawizard2539/jcp, "
            + "schrottplatz, and MrDudle");
        System.out.println("With contributions from others; send \"help authors\"");
        System.out.println("and \"help packwriters\" to your bot in a pm for ");
        System.out.println("other contributors.");
        System.out.println();
        if (args.length > 0)
        {
            doWithArguments(args);
            return;
        }
        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            public void run()
            {
                System.out.println();
                System.out.println("Waiting on log queue to shut down...");
                synchronized (logQueueLock)
                {
                    logQueueRunning = false;
                    int discarded = (logQueue == null ? 0 : logQueue.size());
                    System.out.println("Log queue has shut down. " + discarded
                        + " log event" + (discarded == 1 ? " was" : "s were")
                        + " discarded.");
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
            System.out
                    .println("To set up your bot, run \"jzbot addserver <name> <protocol> <server> ");
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
            System.out.println("server. <password> is entirely optional. <name> ");
            System.out.println("is a name for your server. You'll use this to refer");
            System.out.println("to your server when communicating with your bot. This");
            System.out.println("can be anything.");
            System.out.println("");
            System.out.println("Once you've set up the bot successfully, run \"jzbot\"");
            System.out.println("to actually start your bot.");
            System.out.println("");
            System.out
                    .println("Advanced users can also do \"jzbot addsuperop <hostname>\"");
            System.out.println("or \"jzbot config\" or \"jzbot config <varname>\" or");
            System.out.println("\"jzbot config <varname> <newvalue>\".");
        }
        else if (args[0].equals("addserver"))
        {
            ArrayList<String> list = new ArrayList<String>(Arrays.asList(args));
            list.remove(0);
            args = list.toArray(new String[0]);
            if (args.length < 6 || args.length > 7)
            {
                System.out.println("\"jzbot setup\" expects either 6 or 7 "
                    + "arguments, but you provided " + args.length);
                System.out.println("arguments. See \"jzbot help\" for help.");
                return;
            }
            String serverName = args[0];
            String protocol = args[1];
            String server = args[2];
            String portString = args[3];
            String nick = args[4];
            String hostname = args[5];
            String password = (args.length > 6 ? args[6] : "");
            if (!isOkServerName(serverName))
            {
                System.out.println("That server name is not valid. Server names must");
                System.out.println("contain only lowercase letters, numbers, and hyphens.");
                return;
            }
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
            if (storage.getServer(serverName) != null)
            {
                System.out.println("There is already a server using that name. ");
                System.out.println("Try another name.");
                return;
            }
            Server datastoreServer = storage.createServer();
            datastoreServer.setActive(true);
            datastoreServer.setName(serverName);
            datastoreServer.setProtocol(protocol);
            datastoreServer.setNick(nick);
            datastoreServer.setPassword(password);
            datastoreServer.setPort(port);
            datastoreServer.setServer(server);
            Operator op = storage.createOperator();
            op.setHostname(hostname);
            datastoreServer.getOperators().add(op);
            storage.getServers().add(datastoreServer);
            System.out.println("");
            System.out.println("A new server has been added. Run \"jzbot\"");
            System.out.println("to start your bot.");
        }
        else if (args[0].equals("config"))
        {
            initProxyStorage();
            if (args.length == 1)
            {
                System.out.println("All config var names: "
                    + StringUtils.delimited(ConfigVars.values(), new ToString<ConfigVars>()
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
        // else if (args[0].equals("switchnick"))
        // {
        // if (args.length > 1)
        // {
        // initProxyStorage();
        // config.setNick(args[1]);
        // System.out.println("Successfully set the bot's nick to be \"" + args[1]
        // + "\".");
        // }
        // else
        // {
        // System.out.println("You need to specify a new nickname for the "
        // + "bot to use. Try \"jzbot switchnick <newnick>\".");
        // }
        // }
        else
        {
            System.out.println("That's an invalid command. Try \"jzbot help\".");
        }
    }
    
    private static boolean isOkServerName(String serverName)
    {
        return serverName.matches("^[a-z0-9\\-]*$");
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
        loadCommand(new JoinCommand());
        loadCommand(new LeaveCommand());
        // loadCommand(new LengthCommand());
        loadCommand(new ListChannelsCommand());
        loadCommand(new MMCommand());
        loadCommand(new RedefineCommand());
        loadCommand(new RegexCommand());
        loadCommand(new RestartCommand());
        // loadCommand(new RouletteCommand());
        // loadCommand(new SayCommand());
        loadCommand(new ServerCommand());
        loadCommand(new ShutdownCommand());
        loadCommand(new StatusCommand());
        loadCommand(new SuperopCommand());
        loadCommand(new SwitchnickCommand());
        loadCommand(new TriggerCommand());
        // loadCommand(new TTTCommand());
        // loadCommand(new WeatherCommand());
    }
    
    private static void loadCommand(Command command)
    {
        commands.put(command.getName(), command);
    }
    
    private static void start() throws Throwable
    {
        System.out.println("Initializing...");
        DefaultPastebinProviders.installDefaultSet();
        logsFolder.mkdirs();
        System.out.println("Starting the ProxyStorage system...");
        initProxyStorage();
        if (storage.getServers().size() == 0)
        {
            System.out.println();
            System.out.println("No servers have been added. JZBot will exit.");
            System.out.println("Run \"jzbot help\" for help on how to add a");
            System.out.println("server for your bot to connect to. Specifically,");
            System.out.println("you'll want to look at the addserver command.");
            System.exit(0);
        }
        System.out.println("Starting the relational data store...");
        startRelationalStore();
        System.out.println("Loading core components...");
        loadCommands();
        loadCachedConfig();
        startLogSinkThread();
        reloadRegexes();
        System.out.println("Running _onstartup notifications...");
        runNotificationFactoid(null, null, null, null, "", "_onstartup", new String[0],
                true, false);
        System.out.println("Starting connection cycle thread...");
        startConnectionCycleThread();
        System.out.println("Dispatching notifications to connection cycle thread...");
        notifyConnectionCycleThread();
        System.out.println("Connect thread started.");
        System.out.println();
        System.out.println("JZBot has successfully started up. Server "
            + "connections will be established in a few seconds.");
        System.out.println();
    }
    
    public static java.sql.Connection relationalStore;
    
    public static void startRelationalStore()
    {
        try
        {
            Class.forName("org.h2.Driver");
            File location = new File("storage/relational/rs");
            relationalStore =
                    DriverManager.getConnection("jdbc:h2:" + location.getPath()
                        + ";FILE_LOCK=SOCKET", "sa", "");
            Statement statement = relationalStore.createStatement();
            // statement.execute("create user if not exists jzbot password 'pass'");
            // Now we'll set up aliases to all public static methods in
            // PublicDatabaseUtils.
            Method[] methods = PublicDatabaseUtils.class.getMethods();
            for (Method method : methods)
            {
                if (method.getName().startsWith("metadata_"))
                {
                    statement.execute("create alias if not exists " + method.getName()
                        + " for \"jw.jzbot.PublicDatabaseUtils." + method.getName() + "\"");
                }
            }
            statement.close();
            // relationalStore.close();
            // relationalStore =
            // DriverManager.getConnection("jdbc:h2:" + location.getPath()
            // + ";FILE_LOCK=SOCKET", "jzbot", "pass");
        }
        catch (Exception e)
        {
            try
            {
                relationalStore.close();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
            relationalStore = null;
            throw new RuntimeException("Could not connect to the relational data store.", e);
        }
    }
    
    private static void initProxyStorage()
    {
        proxyStorage =
                new ProxyStorage<Storage>(Storage.class, new File("storage/db"), 20, 30, 10);
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
        logQueue =
                new LinkedBlockingQueue<LogEvent>(Integer.parseInt(ConfigVars.lqmaxsize
                        .get()));
        logQueueDelay = Integer.parseInt(ConfigVars.lqdelay.get());
    }
    
    public static void onJoin(Server datastoreServer, String serverName, String channel,
            String sender, String login, String hostname)
    {
        System.out.println("join detected at " + serverName + " on " + channel + " by "
            + sender);
        Channel chan = datastoreServer.getChannel(channel);
        if (chan == null)
            return;
        logEvent(serverName, channel, "joined", sender, login + "@" + hostname);
        if (sender.equals(getRealConnection(serverName).getConnection().getNick()))
        {
            runNotificationFactoid(serverName, datastoreServer, channel, chan, sender,
                    "_selfjoin", null, true, true);
        }
        else
        {
            runNotificationFactoid(serverName, datastoreServer, channel, chan, sender,
                    "_onjoin", null, true, true);
        }
    }
    
    public static void onPart(Server datastoreServer, String serverName, String channel,
            String sender, String login, String hostname)
    {
        logEvent(serverName, channel, "left", sender, "Left the channel");
        runNotificationFactoid(serverName, datastoreServer, channel, null, sender,
                "_onpart", null, true, true);
    }
    
    public static void onBeforeQuit(Server datastoreServer, String serverName,
            String sourceNick, String sourceLogin, String sourceHostname, String reason)
    {
        Connection con = getRealConnection(serverName).getConnection();
        for (String channel : con.getChannels())
        {
            if (getUser(con, channel, sourceNick) != null)
            {
                logEvent(serverName, channel, "left", sourceNick, "Quit: " + reason);
                runNotificationFactoid(serverName, datastoreServer, channel, null,
                        sourceNick, "_onquit", null, true, true);// should cascade here be
                // false? (to rely on the quit notification itself)
            }
        }
    }
    
    public static HashMap<String, String> channelTopics = new HashMap<String, String>();
    
    public static void onTopic(Server datastoreServer, String serverName, String channel,
            String topic, String setBy, long date, boolean changed)
    {
        channelTopics.put("@" + serverName + channel, topic);
        if (changed)
        {
            logEvent(serverName, channel, "topic", setBy, topic);
            runNotificationFactoid(serverName, datastoreServer, channel, null, setBy,
                    "_ontopic", new String[]
                    {
                            topic, "" + date
                    }, true, true);
        }
    }
    
    public static void onMode(Server datastoreServer, String serverName, String channel,
            String sourceNick, String sourceLogin, String sourceHostname, String mode)
    {
        logEvent(serverName, channel, "mode", sourceNick, mode);
        runNotificationFactoid(serverName, datastoreServer, channel, null, sourceNick,
                "_onmode", new String[]
                {
                    mode
                }, true, true);
    }
    
    public static void onNickChange(Server datastoreServer, String serverName,
            String oldNick, String login, String hostname, String newNick)
    {
        Connection con = getRealConnection(serverName).getConnection();
        for (String channel : con.getChannels())
        {
            if (getUser(con, channel, newNick) != null)
            {
                logEvent(serverName, channel, "nick", oldNick, newNick);
                runNotificationFactoid(serverName, datastoreServer, channel, null, newNick,
                        "_onrename", new String[]
                        {
                                oldNick, newNick
                        }, true, true);
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
    private static void runNotificationFactoid(String serverName, Server server,
            String channelName, Channel chan, String sender, String factname,
            String[] args, boolean timed, boolean cascade)
    {
        System.out.println("Running notification factoid on server " + serverName
            + " channel " + channelName + ", with sender " + sender + ": " + factname);
        if (!factname.startsWith("_"))
            System.err.println("Factoid notification name \"" + factname
                + "\" doesn't start with an underscore. All factoid "
                + "notification names must start with an underscore.");
        if (args == null)
            args = new String[0];
        ArrayList<Factoid> facts = new ArrayList<Factoid>();
        if (serverName == null)
        {
            facts.addAll(Arrays.asList(storage.searchFactoids(factname)));
            facts.addAll(Arrays.asList(storage.searchFactoids(factname + "_*")));
        }
        else if (channelName == null)
        {
            if (server == null)
                server = storage.getServer(serverName);
            if (server == null)
                return;
            facts.addAll(Arrays.asList(server.searchFactoids(factname)));
            facts.addAll(Arrays.asList(server.searchFactoids(factname + "_*")));
            if (cascade)
            {
                facts.addAll(Arrays.asList(storage.searchFactoids("_server" + factname)));
                facts.addAll(Arrays.asList(storage.searchFactoids("_server" + factname
                    + "_*")));
            }
        }
        else
        {
            if (server == null)
                server = storage.getServer(serverName);
            if (server == null)
                return;
            if (chan == null)
                chan = server.getChannel(channelName);
            if (chan == null)
                return;
            facts.addAll(Arrays.asList(chan.searchFactoids(factname)));
            facts.addAll(Arrays.asList(chan.searchFactoids(factname + "_*")));
            if (cascade)
            {
                facts.addAll(Arrays.asList(server.searchFactoids("_chan" + factname)));
                facts.addAll(Arrays
                        .asList(server.searchFactoids("_chan" + factname + "_*")));
                facts.addAll(Arrays.asList(storage.searchFactoids("_chan" + factname)));
                facts.addAll(Arrays.asList(storage
                        .searchFactoids("_chan" + factname + "_*")));
            }
        }
        TimedKillThread tkt = new TimedKillThread(Thread.currentThread());
        // FIXME: make this configurable (up to a hard-coded limit of, say, 3 minutes) by
        // a config variable, and then default it to 40 seconds or something
        tkt.maxRunTime = 75 * 1000;
        if (timed)
            tkt.start();
        try
        {
            for (Factoid f : facts)
            {
                if (f != null)
                {
                    incrementIndirectRequests(f);
                    // FIXME: this means notification factoids can't tell if a user is a
                    // superop. We need to somehow include the hostname of the user here.
                    // UPDATE: how the heck can a user running a notification factoid be a
                    // superop? Notification factoids are run spontaneously by the server,
                    // without any user input, so there isn't a "user" that runs them. On
                    // the other hand, some notifications are triggered by users (like
                    // when a user joins); maybe this is what I was thinking about.
                    String pseudoChannel = channelName;
                    String pseudoServer = serverName;
                    if (pseudoServer == null || pseudoChannel == null)
                    {
                        String primary = ConfigVars.primary.get();
                        try
                        {
                            pseudoServer = extractServerName(primary);
                            pseudoChannel = extractChannelName(primary);
                        }
                        catch (Exception e)
                        {
                            new Exception(
                                    "Exception occurred while extracting "
                                        + "primary channel in notification factoid. The notification "
                                        + "will run, but output generated by it will be silently discarded.",
                                    e).printStackTrace();
                        }
                    }
                    String factValue =
                            safeRunFactoid(f, server, serverName, channelName,
                                    new ServerUser(serverName, sender, null),
                                    new ServerChannel(pseudoServer, pseudoChannel), args,
                                    true, new HashMap<String, String>());
                    ConnectionWrapper con = getConnection(pseudoServer);
                    if (con != null)
                    {
                        if (factValue.trim().equals(""))
                            ;
                        else if (factValue.startsWith("<ACTION>"))
                            con.sendAction(pseudoChannel, factValue.substring("<ACTION>"
                                    .length()));
                        else
                            con.sendMessage(pseudoChannel, factValue);
                    }
                }
            }
        }
        finally
        {
            tkt.active = false;
        }
    }
    
    public static void sendActionOrMessage(ConnectionWrapper target, String channel,
            String message)
    {
        if (message.equals(""))
            ;
        else if (message.startsWith("<ACTION>"))
            target.sendAction(channel, message.substring("<ACTION>".length()));
        else
            target.sendMessage(channel, message);
    }
    
    public static void sendActionOrMessage(Messenger target, String message)
    {
        if (message.equals(""))
            ;
        else if (message.startsWith("<ACTION>"))
            target.sendAction(message.substring("<ACTION>".length()));
        else
            target.sendMessage(message);
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
    public static String runFactoid(Factoid factoid, String server, String channel,
            ServerUser sender, Messenger source, String[] args, Map<String, String> vars,
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
        vars.put("0", sender.nick());
        vars.put("who", sender.nick());
        if (channel != null)
            vars.put("channel", channel);
        String selfName = null;
        if (server != null)
        {
            vars.put("server", server);
            Connection realCon = getRealConnection(server).getConnection();
            selfName = realCon.getNick();
            vars.put("self", selfName);
        }
        vars.put("source", channel == null ? sender.nick() : channel);
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
        context.setServer(server);
        context.setSelf(selfName);
        context.setSource(source);
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
        // FIXME: add a server level here
        any, exact, global
    }
    
    public static String doFactImport(String server, String channel,
            ArgumentList arguments, ServerUser sender, Messenger source,
            boolean allowRestricted, FactQuota quota, ImportLevel level)
    {
        return doFactImport(server, channel, arguments, sender, source, allowRestricted,
                quota, level, null);
    }
    
    public static String doFactImport(String server, String channel,
            ArgumentList arguments, ServerUser sender, Messenger source,
            boolean allowRestricted, FactQuota quota, ImportLevel level,
            Map<String, String> cascadingVars)
    {
        Factoid f = null;
        boolean channelSpecific = false;
        boolean serverSpecific = false;
        Server sv = null;
        Channel cn = null;
        String factname = arguments.getString(0);
        if (server != null)
        {
            sv = storage.getServer(server);
            if (channel != null)
                cn = sv.getChannel(channel);
        }
        // First, we'll try channel-specific lookup.
        if (cn != null)
        {
            f = cn.getFactoid(factname);
            if (f != null)
                channelSpecific = true;
        }
        // Now we'll try server-specific lookup.
        if (f == null && sv != null)
        {
            f = sv.getFactoid(factname);
            if (f != null)
                serverSpecific = true;
        }
        // Now we'll try global lookup.
        if (f == null)
        {
            f = JZBot.storage.getFactoid(factname);
        }
        // Make sure we got one
        if (f == null)
            throw new RuntimeException("That factoid (\"" + arguments.getString(0)
                + "\") doesn't exist at the server \"" + server + "\" and channel \""
                + channel + "\", so you can't import it.");
        Map<String, String> varMap = new HashMap<String, String>();
        if (cascadingVars != null)
            varMap.putAll(cascadingVars);
        incrementIndirectRequests(f);
        return runFactoid(f, server, channel, sender, source, arguments.subList(1)
                .evalToArray(), varMap, allowRestricted, quota);
    }
    
    public static void onKick(Server datastoreServer, String serverName, String channel,
            String kickerNick, String kickerLogin, String kickerHostname,
            String recipientNick, String reason)
    {
        logEvent(serverName, channel, "kick", kickerNick, recipientNick + " " + reason);
        runNotificationFactoid(serverName, datastoreServer, channel, null, kickerNick,
                "_onkick", new String[]
                {
                        recipientNick, reason, kickerNick
                }, true, true);
        if (recipientNick.equals(getRealConnection(serverName).getConnection().getNick()))
        {
            // FIXME: wait a configurable amount of time before rejoining, and maybe put
            // this into a thread pool executor to wait, and maybe try to rejoin every few
            // seconds if we can't on the first try. Also, this is really bad to operate
            // on the low-level connection since this join doesn't get logged; this needs
            // to be fixed as soon as possible.
            getRealConnection(serverName).getConnection().joinChannel(channel);
        }
    }
    
    public static void onMessage(Server datastoreServer, String serverName, String channel,
            String sender, String login, String hostname, String message)
    {
        logEvent(serverName, channel, "message", sender, message);
        TimedKillThread tkt = new TimedKillThread(Thread.currentThread());
        tkt.start();
        try
        {
            System.out.println("Message at " + serverName + " channel " + channel
                + " from " + sender + ": " + message);
            Channel chan = datastoreServer.getChannel(channel);
            if (chan == null)
            {
                System.out.println("No matching channel, probably means "
                    + "we've joined a channel without storage");
                return;
            }
            boolean processFactoids =
                    processChannelRegex(datastoreServer, serverName, channel, sender,
                            hostname, message, false);
            String trigger = chan.getTrigger();
            if (trigger != null && message.startsWith(trigger))
            {
                try
                {
                    message = message.substring(trigger.length());
                    boolean replyToPseudo = false;
                    ServerChannel pseudoTarget = new ServerChannel(serverName, channel);
                    if (isSuperop(serverName, hostname))
                    {
                        if (message.startsWith("%"))
                        {
                            replyToPseudo = true;
                            message = message.substring(trigger.length());
                        }
                        if ((message.startsWith("@") || message.startsWith("#"))
                            && message.contains(" "))
                        {
                            pseudoTarget = parseFragment(message, pseudoTarget);
                            message = message.substring(message.indexOf(' ') + 1);
                        }
                    }
                    if (replyToPseudo && pseudoTarget.getChannel() == null)
                    {
                        getConnection(serverName).sendMessage(
                                channel,
                                "You can only instruct the bot to reply to "
                                    + "the target if the target contains a channel.");
                        return;
                    }
                    System.out.println("running message command");
                    runMessageCommand(
                            JZBot.storage.getServer(pseudoTarget.getServerName()),
                            pseudoTarget.getServerName(), pseudoTarget.getChannel(), false,
                            datastoreServer, serverName, sender,
                            replyToPseudo ? pseudoTarget : new ServerChannel(serverName,
                                    channel), hostname, login, message, processFactoids);
                }
                catch (Throwable e)
                {
                    e.printStackTrace();
                    getConnection(serverName).sendMessage(channel,
                            "Internal upper-propegation error: " + pastebinStack(e));
                }
            }
            else
            {
                System.out.println("Incorrect trigger");
            }
        }
        catch (FactTimeExceededError e)
        {
            getConnection(serverName).sendMessage(channel,
                    "Time exceeded: " + pastebinStack(e));
        }
        finally
        {
            tkt.active = false;
        }
    }
    
    public static void onNotice(Server datastoreServer, String serverName,
            String sourceNick, String sourceLogin, String sourceHostname, String target,
            String line)
    {
        Connection con = getRealConnection(serverName).getConnection();
        boolean inPm = target.equals(con.getNick());
        if (!inPm && target.startsWith("#"))
            logEvent(serverName, target, "notice", sourceNick, line);
        runNotificationFactoid(serverName, datastoreServer, inPm ? null : target, null,
                sourceNick, "_onnotice", new String[]
                {
                        line, target
                }, true, true);
        System.out.println("Notice to " + target + " by " + sourceNick + ": " + line);
        
    }
    
    public static void onInvitation(Server datastoreServer, String serverName,
            String channel, String sender, String login, String hostname, String toChannel)
    {
        ServerUser source = new ServerUser(serverName, sender, hostname);
        try
        {
            Command command = commands.get("join");
            command.run(serverName, channel, true, source, source, toChannel
                + " frominvite");
        }
        catch (Throwable e)
        {
            if (e instanceof ResponseException)
                source.sendSpaced(e.getMessage());
            else
                source.sendSpaced("An error occurred while processing your invitation: "
                    + pastebinStack(e));
        }
    }
    
    public static boolean processChannelRegex(Server server, String serverName,
            String channel, String sender, String hostname, String message, boolean action)
    {
        try
        {
            boolean factOverridden = false;
            synchronized (regexLock)
            {
                Map<String, List<String>> smap = regexCache.get(serverName);
                List<String> channelList = (smap == null ? null : smap.get(channel));
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
                    OverrideStatus override =
                            runRegex(server, serverName, channel, sender, hostname,
                                    message, matcher, regex, action);
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
            getConnection(serverName).sendMessage(channel,
                    "Pre-process regex error: " + pastebinStack(e));
            return true;
        }
    }
    
    public static enum OverrideStatus
    {
        override, factoverride, none
    }
    
    /**
     * 
     * @param server
     * @param channel
     * @param sender
     * @param hostname
     * @param message
     * @param action
     * @param regex
     * @return True if this overrides, false if it doesn't
     */
    private static OverrideStatus runRegex(Server server, String serverName,
            String channel, String sender, String hostname, String message,
            Matcher matcher, String regexValue, boolean action)
    {
        Channel c = server.getChannel(channel);
        if (c == null)
            return OverrideStatus.none;
        Regex regex = c.getRegex(regexValue);
        if (regex == null)
            return OverrideStatus.none;
        Factoid f = c.getFactoid(regex.getFactoid());
        if (f == null)
            f = server.getFactoid(regex.getFactoid());
        if (f == null)
            f = JZBot.storage.getFactoid(regex.getFactoid());
        if (f == null)
        {
            getConnection(serverName).sendMessage(channel,
                    "Invalid factoid in regex " + regexValue + ": " + regex.getFactoid());
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
        String factValue =
                safeRunFactoid(f, server, serverName, channel, new ServerUser(serverName,
                        sender, hostname), new ServerChannel(serverName, channel), strings,
                        true, vars);
        sendActionOrMessage(getConnection(serverName), channel, factValue);
        if ("true".equalsIgnoreCase(vars.get("__internal_override")))
            return OverrideStatus.override;
        else if ("true".equalsIgnoreCase(vars.get("__fact_override")))
            return OverrideStatus.factoverride;
        return OverrideStatus.none;
    }
    
    public static void onAction(Server datastoreServer, String serverName, String sender,
            String login, String hostname, String channel, String action)
    {
        System.out.println("action received on @" + serverName + "!" + sender + channel
            + ": " + action);
        if (!(channel.startsWith("#")))
            // We don't support actions sent in a pm right now. These might be supported
            // at a later time.
            return;
        logEvent(serverName, channel, "action", sender, action);
        TimedKillThread tkt = new TimedKillThread(Thread.currentThread());
        tkt.start();
        try
        {
            processChannelRegex(datastoreServer, serverName, channel, sender, hostname,
                    action, true);
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            getConnection(serverName).sendMessage(sender,
                    "Exception while processing action: " + pastebinStack(t));
        }
        finally
        {
            tkt.active = false;
        }
    }
    
    private static void runMessageCommand(Server datastoreServer, String serverName,
            String channel, boolean pm, Server senderServer, String senderServerName,
            String sender, Messenger source, String hostname, String username,
            String message, boolean processFactoids)
    {
        ServerUser serverUser = new ServerUser(senderServerName, sender, hostname);
        ServerChannel serverChannel = null;
        if (channel != null)
            serverChannel = new ServerChannel(serverName, channel);
        if (source == null)
        {
            if (pm)
                source = serverUser;
            else
                source = serverChannel;
        }
        // ConnectionWrapper con = getConnection(serverName);
        // ConnectionWrapper senderCon = getConnection(senderServerName);
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
                c.run(serverName, channel, pm, serverUser, source, commandArguments);
            }
            catch (Exception e)
            {
                if (e instanceof ResponseException)
                {
                    if (!(e instanceof DieException))
                        source.sendSpaced(((ResponseException) e).getMessage());
                }
                else
                {
                    e.printStackTrace();
                    source.sendSpaced("An error occured while running the command "
                        + command + ": " + pastebinStack(e));
                }
            }
            System.out.println("Finishing command run #1");
            return;
        }
        /*
         * If we get here, then the text isn't a command. We'll check to see if it's a
         * factoid.
         * 
         * Our first check will be for a channel-specific factoid. However, we should just
         * return if we're not supposed to process factoids, which occurrs if a regex
         * filtered out a call with override or factoverride.
         * 
         * And actually, that brings to mind another task that needs to be done, and I'm
         * putting it here in case I forget it: override and factoverride set local
         * varaibles prefixed with two underscores. Some mechanism that doesn't use local
         * variables needs to be added.
         */
        if (!processFactoids)
        {
            System.out.println("Finishing command run #2");
            return;
        }
        Factoid factoid = null;
        // Check for a channel-specific factoid
        if (channel != null)
        {
            Channel cn = datastoreServer.getChannel(channel);
            if (cn != null)
            {
                factoid = cn.getFactoid(command);
            }
        }
        // Check for a server-specific factoid
        if (factoid == null)
            factoid = datastoreServer.getFactoid(command);
        // Check for a global factoid
        if (factoid == null)
            factoid = storage.getFactoid(command);
        if (factoid != null)
        {
            if (factoid.isLibrary())
            {
                source.sendMessage("That factoid is a library factoid. It can only be run "
                    + "by importing it, by creating a regex "
                    + "that uses it, by using it as "
                    + "a trigger, and so on. Run \"factoid unlibrary " + command
                    + "\" if you want to remove this factoid's library status.");
                System.out.println("Finishing command run #5");
                return;
            }
            incrementDirectRequests(factoid);
            System.out.println("requests incremented");
            String factValue;
            System.out.println("calculating fact value");
            factValue =
                    safeRunFactoid(factoid, datastoreServer, serverName, channel,
                            serverUser, source, commandArguments.split(" "), isSuperop(
                                    serverName, hostname), new HashMap<String, String>());
            System.out.println("fact value: " + factValue);
            sendActionOrMessage(source, factValue);
            System.out.println("Finishing command run #6");
            return;
        }
        System.out.println("invalid command");
        doInvalidCommand(pm, pm ? senderServer : datastoreServer, pm ? senderServerName
                : serverName, channel, serverUser, source);
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
    
    public static String safeRunFactoid(Factoid f, Server server, String serverName,
            String channel, ServerUser sender, Messenger source, String[] arguments,
            boolean allowRestricted, Map<String, String> vars)
    {
        String factValue;
        try
        {
            factValue =
                    runFactoid(f, serverName, channel, sender, source, arguments, vars,
                            allowRestricted, null);
        }
        catch (FactoidException e)
        {
            factValue = "Syntax exception while running factoid: " + pastebinStack(e);
        }
        catch (FactTimeExceededError e)
        {
            factValue = "The factoid took too long to run: " + pastebinStack(e);
        }
        catch (StackOverflowError e)
        {
            factValue =
                    "A stack overflow occurred. This probably means you have an infinitely-recursive loop in your factoid. Details: "
                        + pastebinStack(e);
        }
        catch (Exception e)
        {
            factValue = "External exception while running factoid: " + pastebinStack(e);
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
            eString =
                    ((FactoidException) e).createFactoidStackTrace()
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
    
    private static void doInvalidCommand(boolean pm, Server server, String serverName,
            String channel, ServerUser sender, Messenger source)
    {
        Channel c = server.getChannel(channel);
        String notfoundFact = ConfigVars.notfound.get();
        if (notfoundFact.trim().equals("") && c != null)
        {
            source.sendMessage("Huh? (pm \"help\" for more info)");
        }
        else if (notfoundFact.trim().equals(""))
        {
            source.sendMessage("Huh? (pm \"help\" for more info)");
        }
        else
        {
            try
            {
                Factoid f = null;
                if (c != null)
                    f = c.getFactoid(notfoundFact);
                if (f == null)
                    f = server.getFactoid(notfoundFact);
                if (f == null)
                    f = storage.getFactoid(notfoundFact);
                if (f == null)
                    throw new RuntimeException("The not-found factoid \"" + notfoundFact
                        + "\" does not exist.");
                String factValue =
                        safeRunFactoid(f, server, serverName, channel, sender, source,
                                new String[0], true, new HashMap<String, String>());
                if (factValue.trim().equals(""))
                    factValue = "Not-found factoid didn't output anything";
                sendActionOrMessage(source, factValue);
            }
            catch (Throwable t)
            {
                sender.sendMessage(pm, serverName, channel,
                        "Syntax error in not-found factoid: " + pastebinStack(t));
            }
        }
    }
    
    public static void onConnect(final Server datastoreServer, final String serverName)
    {
        System.out.println("running onConnect() for server " + serverName);
        final ConnectionWrapper con = getConnection(serverName);
        new Thread()
        {
            public void run()
            {
                if (ConfigVars.servicemsg.get().equals("1"))
                {
                    try
                    {
                        Thread.sleep(2500);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    System.out.println("Authenticating with NickServ via privmsg");
                    String pwd = config.getPassword();
                    if (pwd != null && !"".equals(pwd))
                        con.sendMessage("NickServ", "identify " + pwd);
                }
                try
                {
                    Thread.sleep(2300);// FIXME: make this a config var
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                for (Channel channel : datastoreServer.getChannels().isolate())
                {
                    if (!channel.isSuspended())
                    {
                        try
                        {
                            Thread.sleep(2300);
                        }
                        catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                        System.out.println(serverName + ": joining " + channel.getName());
                        con.joinChannel(channel.getName());
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
                try
                {
                    runNotificationFactoid(serverName, datastoreServer, null, null, con
                            .getConnection().getNick(), "_onconnect", new String[0], true,
                            true);
                }
                catch (Throwable e)
                {
                    new Exception("Exception occurred while running "
                        + "_onconnect for server \"" + serverName + "\"", e)
                            .printStackTrace();
                }
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
                    con.setMode(con.getNick(), "+" + c);
                }
            }
        }.start();
    }
    
    public static void onDisconnect(Server datastoreServer, String serverName)
    {
        System.out.println("on disconnect");
        // This method is commented out because the connection cycle thread essentially
        // takes care of all of this for us.
        
        // FIXME: the topic map still needs to be cleared; we're not doing that here,
        // which is bad because it lets us get misinformed about a channel's topic when
        // that channel used to have a topic but lost it while we were reconnecting.
        
        // try
        // {
        // elevatedOpMap.clear();
        // elevatedSuperopList.clear();
        // channelTopics.clear();
        // proxyStorage.close();
        // synchronized (httpServers)
        // {
        // for (int port : httpServers.keySet())
        // {
        // httpServers.get(port).stopServer();
        // }
        // httpServers.clear();
        // }
        // initProxyStorage();
        // reloadRegexes();
        // loadCachedConfig();
        // }
        // catch (Exception e)
        // {
        // e.printStackTrace();
        // }
        // System.out.println("starting reconnect thread");
        // new Thread()
        // {
        //            
        // public void run()
        // {
        // int attempts = 0;
        // while (true)
        // {
        // try
        // {
        // attempts++;
        // int time;
        // if (attempts < 5)
        // time = 1;
        // else if (attempts < 10)
        // time = 5;
        // else if (attempts < 20)
        // time = 15;
        // else if (attempts < 40)
        // time = 30;
        // else if (attempts < 70)
        // time = 60;
        // else if (attempts < 100)
        // time = 120;
        // else
        // time = 240;
        // // This is to make sure that we don't flood ourselves
        // // off again after we join
        // if (manualReconnect)
        // manualReconnect = false;
        // else
        // time += 30;
        // Thread.sleep(time * 1000);
        // bot.reconnect();
        // }
        // catch (Exception e)
        // {
        // e.printStackTrace();
        // continue;
        // }
        // return;
        // }
        // }
        // }.start();
    }
    
    public static void onPrivateMessage(Server datastoreServer, String serverName,
            String sender, String login, String hostname, String message)
    {
        TimedKillThread tkt = new TimedKillThread(Thread.currentThread());
        tkt.start();
        ConnectionWrapper con = getConnection(serverName);
        try
        {
            ServerChannel pseudoTarget = new ServerChannel(serverName, null);
            boolean replyToPseudo = false;
            System.out.println("pm from " + sender + ": " + message);
            if (message.contains(" ") && isSuperop(serverName, hostname))
            {
                if (message.startsWith("%"))
                {
                    message = message.substring(1);
                    replyToPseudo = true;
                }
                if (message.startsWith("@") || message.startsWith("#"))
                {
                    pseudoTarget = parseFragment(message, pseudoTarget);
                    message = message.substring(message.indexOf(" ") + 1);
                }
            }
            if (replyToPseudo && pseudoTarget.getChannel() == null)
            {
                con.sendMessage(sender, "You can only instruct the bot to reply to "
                    + "the target if the target contains a channel.");
                return;
            }
            try
            {
                Server pseudoDatastoreServer =
                        storage.getServer(pseudoTarget.getServerName());
                if (replyToPseudo)
                {
                    runMessageCommand(pseudoDatastoreServer, pseudoTarget.getServerName(),
                            pseudoTarget.getChannel(), false, datastoreServer, serverName,
                            sender, null, hostname, login, message, true);
                }
                else
                {
                    runMessageCommand(pseudoDatastoreServer, pseudoTarget.getServerName(),
                            pseudoTarget.getChannel(), true, datastoreServer, serverName,
                            sender, null, hostname, login, message, true);
                }
            }
            catch (Throwable e)
            {
                e.printStackTrace();
                con.sendMessage(sender, "Internal upper-propegating pm exception: "
                    + pastebinStack(e));
            }
        }
        catch (FactTimeExceededError e)
        {
            con.sendMessage(sender, "Time exceeded: " + pastebinStack(e));
        }
        finally
        {
            tkt.active = false;
        }
        
    }
    
    public static ServerChannel parseFragment(String message, ServerChannel scope)
    {
        String pseudoServer = scope.getServerName();
        String pseudoChannel = scope.getChannel();
        if (message.startsWith("#"))
        {
            pseudoChannel = message.substring(0, message.indexOf(" "));
            message = message.substring(message.indexOf(" ") + 1);
        }
        else if (message.startsWith("@"))
        {
            String fragment = message.substring(0, message.indexOf(" "));
            message = message.substring(message.indexOf(" ") + 1);
            if (fragment.contains("#"))
            {
                pseudoServer = fragment.substring(1, fragment.indexOf("#"));
                pseudoChannel = fragment.substring(fragment.indexOf("#"));
            }
            else
            {
                pseudoServer = fragment.substring(1);
                pseudoChannel = null;
            }
        }
        return new ServerChannel(pseudoServer, pseudoChannel);
    }
    
    private static ArrayList<String> elevatedSuperopList = new ArrayList<String>();
    
    public static void elevate(String serverName, String hostname, String channel)
    {
        elevatedSuperopList.add(serverName + "@" + hostname);
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
    
    public static User getUser(Connection con, String channel, String nick)
    {
        User[] users = con.getUsers(channel);
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
    
    public static void sendDelimited(ConnectionWrapper bot, String[] array,
            String delimiter, String recipient)
    {
        JZUtils.ircSendDelimited(array, delimiter, new GenericMessenger(bot, recipient));
    }
    
    public static Factoid getChannelFactoid(Server server, String channelName,
            String factoid)
    {
        Channel channel = server.getChannel(channelName);
        if (channel == null)
            throw new ResponseException("No such channel: " + channel);
        return channel.getFactoid(factoid);
    }
    
    public static Factoid getServerFactoid(Server server, String factoid)
    {
        return server.getFactoid(factoid);
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
            synchronized (connectionCycleLock)
            {
                for (ConnectionContext con : connectionMap.values())
                    con.getConnection().setEncoding(charset);
            }
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
    
    /**
     * Maps server names to a map that maps channel names to a list of all regexes at the
     * channel.
     */
    private static Map<String, Map<String, List<String>>> regexCache =
            new HashMap<String, Map<String, List<String>>>();
    
    private static Object regexLock = new Object();
    
    private static String configNolog;
    
    private static int configLogsize = 0;
    
    public static final long startedAtTime = System.currentTimeMillis();
    
    public static void reloadRegexes()
    {
        synchronized (regexLock)
        {
            regexCache.clear();
            for (Server server : storage.getServers().isolate())
            {
                Map<String, List<String>> thisServerMap =
                        new HashMap<String, List<String>>();
                regexCache.put(server.getName(), thisServerMap);
                for (Channel c : server.getChannels().isolate())
                {
                    ArrayList<String> list = new ArrayList<String>();
                    thisServerMap.put(c.getName(), list);
                    for (Regex regex : c.getRegularExpressions().isolate())
                    {
                        list.add(regex.getExpression());
                    }
                }
            }
        }
    }
    
    public static boolean isChannelOp(String serverName, String channel, String sender)
    {
        User[] users = getRealConnection(serverName).getConnection().getUsers(channel);
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
    
    public static void logEvent(String server, String channel, String event, String nick,
            String details)
    {
        if (server.startsWith("@"))
            server = server.substring(1);
        LogEvent e = new LogEvent();
        e.server = server;
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
        Thread thread = new Thread("log-sink-thread")
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
        String server = logEvent.server;
        String channel = logEvent.channel;
        String nick = logEvent.nick;
        String details = logEvent.details;
        String event = logEvent.event;
        String filename = "@" + server + channel;
        details = details.replace("\r", "").replace("\n", "");
        try
        {
            if (StringUtils.isMemberOf(filename, configNolog.split("\\|")))
                return;
            String data =
                    event + " " + System.currentTimeMillis() + " " + nick + " " + details;
            File logFile = new File(logsFolder, filename);
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
            Factpack pack;
            try
            {
                pack = Factpack.parse(StringUtils.readFile(file));
            }
            catch (Exception e)
            {
                throw new RuntimeException("Exception while processing file "
                    + file.getAbsolutePath(), e);
            }
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
    
    /**
     * Calls {@link #pastebinNotice(String, Feature[])}. If an exception is thrown,
     * <tt>alternative</tt> is returned, with "(pastebin unavailable) " prefixed.
     * 
     * @param text
     *            The text to pastebin
     * @param features
     *            The features that the target pastebin should have
     * @param alternative
     *            The text to return if the pastebin service is unavailable
     * @return the return value of pastebinNotice(String, Feature[]), or
     *         <tt>"(pastebin unavailable) " + alternative</tt> if the pastebin service is
     *         currently unavailable
     */
    public static String tryPastebin(String text, Feature[] features, String alternative)
    {
        try
        {
            return pastebinNotice(text, features);
        }
        catch (Exception e)
        {
            return "(pastebin unavailable) " + alternative;
        }
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
    
    public static String getDefaultPartMessage(String serverName, String channel)
    {
        return "Later everyone.";
    }
    
    public static String failsafeExtractServerName(String string)
    {
        try
        {
            return extractServerName(string);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return "Exception while getting server via JZBot.failsafeExtractServerName("
                + string + ")";
        }
    }
    
    public static String failsafeExtractChannelName(String string)
    {
        try
        {
            return extractChannelName(string);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return "Exception while getting channel via JZBot.failsafeExtractChannelName("
                + string + ")";
        }
    }
    
    /**
     * Returns getConnection(server).
     * 
     * @param server
     * @return
     */
    public static ConnectionWrapper getServer(String server)
    {
        return getConnection(server);
    }
    
    public static ConnectionWrapper getCheckedConnection(String serverName)
    {
        ConnectionWrapper con = getConnection(serverName);
        if (con == null)
            throw new FactoidException("There is no server for the server name "
                + serverName + ", but this command requires a valid server name.");
        return con;
    }
    
}
