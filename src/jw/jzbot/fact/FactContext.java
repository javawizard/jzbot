package jw.jzbot.fact;

import java.util.*;

import jw.jzbot.ConnectionWrapper;
import jw.jzbot.JZBot;
import jw.jzbot.fact.ast.FactEntity;
import jw.jzbot.fact.ast.Sequence;
import jw.jzbot.fact.debug.DebugInstance;
import jw.jzbot.fact.debug.DebugSupport;
import jw.jzbot.fact.exceptions.FactoidException;
import jw.jzbot.scope.Messenger;
import jw.jzbot.scope.Scope;
import jw.jzbot.scope.UserMessenger;
import jw.jzbot.storage.Channel;
import jw.jzbot.storage.Server;

import org.jdom.Document;

public class FactContext implements Scope
{
    /**
     * A message that can be used as exception messages to indicate that a server was
     * needed but the scope does not contain one.
     */
    private static final String NO_SCOPED_SERVER =
            "The current scope does not have an associated "
                    + "server, but a server was needed. Consider "
                    + "wrapping this function call with a call to "
                    + "the {scope} function to add a server to the current scope.";
    private static final String NO_SCOPED_CHANNEL =
            "The current scope does not have an associated "
                    + "channel, but a server was needed. Consider "
                    + "wrapping this function call with a call to "
                    + "the {scope} function to add a channel to the current scope.";
    
    public FactContext()
    {
        this(null);
    }

    public FactContext(FactContext parentContext) {
        this.parentContext = parentContext;
        this.functionArguments = new ArgumentList(new Sequence(), this);

        if (parentContext != null) {
            this.setAction(parentContext.isAction());
            this.setChannel(parentContext.getChannel());
            this.setGlobalVars(parentContext.getGlobalVars());
            // Don't set local vars - the whole point of context nesting is that we get our own local var pool
            this.setQuota(parentContext.getQuota());
            this.setSelf(parentContext.getSelf());
            this.setSender(parentContext.getSender());
            this.setServer(parentContext.getServer());
            this.setSource(parentContext.getSource());
        }
    }

    private FactContext parentContext = null;
    
    private Map<String, String> localVars =
            Collections.synchronizedMap(new HashMap<String, String>());
    private ArgumentList functionArguments = null;
    private Map<String, String> globalVars = JZBot.globalVariables;
    private Map<String, Document> xmlDocuments =
            Collections.synchronizedMap(new HashMap<String, Document>());
    // TODO: replace xmlDocuments with objectStorage. objectStorage is a map that groups
    // of functions can use to register custom storage objects, where the name should be
    // <group>-<whatever>. For example, xml documents will be stored as "xml-<name>",
    // where <name> is the name of the xml document.
    public final Map<String, Object> objectStorage =
            Collections.synchronizedMap(new HashMap<String, Object>());
    private Map<String, FactEntity> storedSubroutines =
            Collections.synchronizedMap(new HashMap<String, FactEntity>());
    
    public Map<String, FactEntity> getStoredSubroutines()
    {
        return storedSubroutines;
    }
    
    public void setStoredSubroutines(Map<String, FactEntity> storedSubroutines)
    {
        this.storedSubroutines = storedSubroutines;
    }
    
    private boolean action;
    private String server;
    private String channel;
    private UserMessenger sender;
    private Messenger source;

    private Set<String> levelNames = Collections.synchronizedSet(new HashSet<String>());
    
    public Messenger getSource()
    {
        return source;
    }
    
    public void setSource(Messenger source)
    {
        this.source = source;
    }
    
    private String self;
    private FactQuota quota = new FactQuota();
    private DebugInstance debugger;
    
    public DebugInstance getDebugger()
    {
        return debugger;
    }
    
    public void installDebugger(DebugSupport support)
    {
        this.debugger = support.createInstance();
    }
    
    public FactQuota getQuota()
    {
        return quota;
    }
    
    public void setQuota(FactQuota quota)
    {
        this.quota = quota;
    }
    
    public Map<String, Document> getXmlDocuments()
    {
        return xmlDocuments;
    }
    
    public String getSelf()
    {
        return self;
    }
    
    public void setSelf(String self)
    {
        this.self = self;
    }
    
    public String getChannel()
    {
        return channel;
    }
    
    public String getChannelName()
    {
        return getChannel();
    }
    
    public void setChannel(String channel)
    {
        this.channel = channel;
    }
    
    public UserMessenger getSender()
    {
        return sender;
    }
    
    public void setSender(UserMessenger sender)
    {
        this.sender = sender;
    }
    
    public boolean isAction()
    {
        return action;
    }
    
    public void setAction(boolean action)
    {
        this.action = action;
    }

    public ArgumentList getFunctionArguments() {
        return functionArguments;
    }

    public void setFunctionArguments(ArgumentList functionArguments) {
        this.functionArguments = functionArguments;
    }

    public FactContext getAncestorAtLevel(int level) {
        FactContext current = this;
        while (level > 0) {
            level--;
            current = current.parentContext;
            if (current == null) {
                throw new RuntimeException("This context doesn't have that many ancestors");
            }
        }
        return current;
    }

    public FactContext getAncestorAtLevel(String level) {
        // If it's the empty string, return this
        if (level.equals(""))
            return this;

        // Try to parse it as an integer level first
        Integer levelAsInt = null;
        try {
            levelAsInt = new Integer(level);
        } catch (NumberFormatException e) {
        }
        if (levelAsInt != null)
            return getAncestorAtLevel(levelAsInt);

        FactContext current = this;
        while (current != null) {
            if (current.levelNames.contains(level))
                return current;
            current = current.parentContext;
        }

        throw new RuntimeException("This context doesn't have an ancestor with the name \"" + level + "\"");
    }

    public Set<String> getLevelNames() {
        return this.levelNames;
    }

    public void addLevelName(String levelName) {
        if (levelName.equals(""))
            throw new RuntimeException("Level names cannot be empty");
        try {
            Integer.parseInt(levelName);
            // Don't allow these for now, since there wouldn't be any way to reference them -
            // getAncestorAtLevel(String) would interpret them as an attempt to access the
            // level that many parents up the ancestry chain. I might change this in the
            // future to try the n-levels-up-the-ancestry-chain method and fall back to
            // treating it as a level name later on...
            throw new RuntimeException("Level names cannot (at the moment) be integers");
        } catch (NumberFormatException e) {
        }
        this.levelNames.add(levelName);
    }
    
    public Map<String, String> getLocalVars()
    {
        return localVars;
    }
    
    public void setLocalVars(Map<String, String> localVars)
    {
        this.localVars = localVars;
    }
    
    public Map<String, String> getGlobalVars()
    {
        return globalVars;
    }
    
    public Map<String, String> getChainVars()
    {
        return quota.getChainVars();
    }
    
    public void setGlobalVars(Map<String, String> globalVars)
    {
        this.globalVars = globalVars;
    }
    
    public void incrementMessageCount()
    {
        quota.incrementMessageCount();
    }
    
    public void incrementImportCount()
    {
        quota.incrementImportCount();
    }
    
    public String getServer()
    {
        return server;
    }
    
    public void setServer(String server)
    {
        this.server = server;
    }
    
    /**
     * Returns the database server object that represents the server that this context is
     * currently scoped to. If there is no such server, or if this context is not scoped
     * to a server, an exception will be thrown.
     * 
     * @return
     */
    public Server checkedGetDatastoreServer()
    {
        Server s = getDatastoreServer();
        if (s == null)
            throw new FactoidException(NO_SCOPED_SERVER);
        return s;
    }

    public Server getDatastoreServer() {
        if (server == null)
            return null;
        return JZBot.storage.getServer(server);
    }

    public Channel checkedGetDatastoreChannel() {
        Channel c = getDatastoreChannel();
        if (c == null)
            throw new FactoidException(NO_SCOPED_CHANNEL);
        return c;
    }

    public Channel getDatastoreChannel() {
        if (channel == null)
            return null;
        Server s = getDatastoreServer();
        if (s == null)
            return null;
        return s.getChannel(channel);
    }
    
    public String getCheckedServer()
    {
        if (server == null)
            throw new FactoidException(NO_SCOPED_SERVER);
        return server;
    }
    
    public ConnectionWrapper getConnection()
    {
        return JZBot.getConnection(server);
    }
    
    /**
     * Gets the connection object associated with this context's server (the context's
     * server name can be seen with {@link #getServer()} and set with
     * {@link #setServer(String)}). If there is no such connection, or if getServer()
     * returns null, a FactoidException is thrown with a message indicating this.
     * 
     * @return The connection object for this context
     * @throws FactoidException
     *             if there is no such connection or getServer() returns null
     */
    public ConnectionWrapper checkedGetConnection()
    {
        ConnectionWrapper con = getConnection();
        if (con == null)
            throw new FactoidException(NO_SCOPED_SERVER);
        return con;
    }
    
    /**
     * Same as {@link #getServer()}. This method exists so that FactContext can implement
     * {@link Scope}.
     * 
     * @return
     */
    @Override
    public String getServerName()
    {
        return getServer();
    }
    
    public String currentScope()
    {
        String result = "";
        if (getServer() != null)
            result += "@" + getServer();
        if (getChannel() != null)
            result += getChannel();
        return result;
    }


    /**
     * Creates a copy of this FactContext that can be used for new threads spawned from
     * this factoid invocation. The new context has new local variable and local function spaces but shares
     * the same persistent, global, and chain variable space with the old context.
     *
     * @param localVarRegex
     *            The regex to check all local vars against. If their names match this
     *            regex, they will be copied into the new context.
     * @return
     */
    public FactContext cloneForThreading(String localVarRegex)
    {
        FactContext context = new FactContext();
        context.setAction(this.isAction());
        context.setChannel(this.getChannel());
        context.setGlobalVars(this.getGlobalVars());
        // Don't set local vars or functions; the context creates new maps for itself.
        context.setQuota(this.getQuota());
        context.setSelf(this.getSelf());
        context.setSender(this.getSender());
        context.setServer(this.getServer());
        context.setSource(this.getSource());
        for (String name : localVars.keySet())
        {
            if (name.matches(localVarRegex))
                context.getLocalVars().put(name, localVars.get(name));
        }
        return context;
    }

    public Function getFunction(String functionName, FunctionScope functionScope) {
        FunctionScope[] scopesToCheck;
        if (functionScope == null)
            scopesToCheck = FunctionScope.values();
        else
            scopesToCheck = new FunctionScope[] {functionScope};
        for (FunctionScope scope : scopesToCheck) {
            Function function = scope.getFunction(this, functionName);
            if (function != null)
                return function;
        }
        throw new FactoidException("No such function at " + Arrays.deepToString(scopesToCheck) + ": " + functionName);
    }

    public Function getLocalFunction(String name) {
        if (this.storedSubroutines.containsKey(name))
            return new DynamicFunction(name, this.storedSubroutines.get(name));
        else
            return null;
    }
    
    /**
     * Called by the Fact interpreter when the user tries to invoke a function that
     * doesn't exist. This method tries to resolve it as a dynamic function using several
     * different methods. If it can't figure out how to run this function, it throws a
     * FactoidException.
     * 
     * @param name
     *            The name of the function to invoke
     * @param sink
     *            The sink to write the function's output to
     * @param arguments
     *            The arguments to the function
     */
    public void invokeDynamicFunction(String name, Sink sink, ArgumentList arguments)
    {
        // First, we'll try to invoke this as a stored subroutine. This essentially
        // shortcuts {something} for {run|something}, except that arguments to {something}
        // are set as %f.1%, %f.2%, etc.
        FactEntity entity = storedSubroutines.get(name);
        if (entity != null)
        {
            invokeDynamicAsStoredSubroutine(entity, sink, arguments);
            return;
        }
        // Stored subroutine shortcutting is the only form of dynamic invocation we're
        // supporting for now. Since that didn't work, we'll throw an exception.
        throw new FactoidException("No such function: " + name);
    }
    
    private void invokeDynamicAsStoredSubroutine(FactEntity entity, Sink sink,
            ArgumentList arguments)
    {
        String[] argumentArray = arguments.evalToArray();
        String[] previousVars = new String[argumentArray.length];
        for (int i = 0; i < argumentArray.length; i++)
        {
            String varName = "f." + (i + 1);
            previousVars[i] = localVars.get(varName);
            localVars.put(varName, argumentArray[i]);
        }
        try
        {
            entity.resolve(sink, this);
        }
        finally
        {
            for (int i = 0; i < argumentArray.length; i++)
            {
                String varName = "f." + (i + 1);
                if (previousVars[i] == null)
                    localVars.remove(varName);
                else
                    localVars.put(varName, previousVars[i]);
            }
        }
    }
    
    @Override
    public String getCanonicalName()
    {
        if (getServer() == null)
            return "";
        if (getChannel() == null)
            return "@" + getServer();
        return "@" + getServer() + getChannel();
    }
    
    @Override
    public String getScopeName()
    {
        return getCanonicalName();
    }
}
