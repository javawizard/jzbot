package jw.jzbot.fact;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import jw.jzbot.ConnectionWrapper;
import jw.jzbot.Scope;
import jw.jzbot.JZBot;
import jw.jzbot.Messenger;
import jw.jzbot.ServerUser;
import jw.jzbot.fact.ast.FactEntity;
import jw.jzbot.fact.debug.DebugInstance;
import jw.jzbot.fact.debug.DebugSupport;
import jw.jzbot.fact.exceptions.FactoidException;
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
    
    public FactContext()
    {
    }
    
    private Map<String, String> localVars =
            Collections.synchronizedMap(new HashMap<String, String>());
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
    private ServerUser sender;
    private Messenger source;
    
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
    
    public void setChannel(String channel)
    {
        this.channel = channel;
    }
    
    public ServerUser getSender()
    {
        return sender;
    }
    
    public void setSender(ServerUser sender)
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
        if (server == null)
            throw new FactoidException(NO_SCOPED_SERVER);
        Server s = JZBot.storage.getServer(server);
        if (s == null)
            throw new FactoidException(NO_SCOPED_SERVER);
        return s;
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
     * this factoid invocation. The new context has a new local variable space but shares
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
        // Don't set local vars; the context creates a new map for itself.
        context.setQuota(this.getQuota());
        context.setSelf(this.getSelf());
        context.setSender(this.getSender());
        context.setSource(this.getSource());
        for (String name : localVars.keySet())
        {
            if (name.matches(localVarRegex))
                context.getLocalVars().put(name, localVars.get(name));
        }
        return context;
    }
    
}
