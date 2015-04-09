package jw.jzbot.fact;

import java.util.*;
import java.util.stream.Stream;

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
        this(null, null);
    }

    public FactContext(FactContext parentContext) {
        this(parentContext, null);
    }

    public FactContext(FactContext parentContext, Long codeVersion) {
        this.parentContext = parentContext;
        this.functionArguments = new ArgumentList(new Sequence(), this);
        this.latestCodeRunVersion = codeVersion;

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
            // If parent accessed a vault, we'll have access to its data, so consider us to have accessed the same
            // vault as well
            this.updateOldestVaultAccessedVersion(parentContext.getOldestVaultAccessedVersion());
            // Make sure that we're not new enough that we shouldn't have access to one of our parent's vaults
            this.validateVaultAccess();
            // Then propagate our version to our parent
            if (this.parentContext != null) {
                this.parentContext.updateLatestCodeRunVersion(codeVersion);
            }
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
    private Map<String, Function> localFunctions =
            Collections.synchronizedMap(new HashMap<String, Function>());
    /**
     * Largest version number of any code (recursively) run as part of this context. This will be the greater of its
     * factoid or function's last created/edited version and the versions (recursively) of all functions invoked. A
     * context without a latestCodeRunVersion is treated as if it were written at an infinite version number, i.e.
     * newer than all existing vaults, and is thus unable to read from a vault without blowing up.
     */
    private Long latestCodeRunVersion = null;
    /**
     * Oldest version number at which all vaults whose data this function has been given access to have been reset.
     * This is updated every time a vault is accessed, and is additionally passed down into all functions invoked from
     * a given function (as they are likely to be given access to sensitive data - at some point I'll add a mechanism
     * for indicating that no such data is to be passed into a given function invocation and that the invoked function
     * may therefore be modified at will). It can also be updated in a parent context with functions like
     * {svflagcaller}, which can be used if e.g. the function in question wants to return sensitive data to its caller.
     *
     * A context without an oldestVaultAccessedVersion is treated as if the only vault it had accessed had been reset
     * at an infinite version number, i.e. no errors will occur.
     */
    private Long oldestVaultAccessedVersion = null;
    
    public Map<String, Function> getLocalFunctions()
    {
        return localFunctions;
    }
    
    public void setLocalFunctions(Map<String, Function> LocalFunctions)
    {
        this.localFunctions = localFunctions;
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
        // TODO: What to do about vaults and versioning? Current behavior forbids all vault access from a thread, but
        // also doesn't propagate code versions up to the caller, which could cause problems if the caller relies on
        // chain variables
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

    public String[] getFunctionNames(String prefix, FunctionScope functionScope) {
        FunctionScope[] scopes;
        if (functionScope == null)
            scopes = FunctionScope.values();
        else
            scopes = new FunctionScope[]{functionScope};
        return Stream.of(scopes)
                .flatMap((scope) -> Stream.<String>of(scope.listFunctionNames(this, prefix)))
                .distinct()
                .toArray((size) -> new String[size]);
    }

    public Function getLocalFunction(String name) {
        return this.localFunctions.get(name);
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

    public Long getLatestCodeRunVersion() {
        return this.latestCodeRunVersion;
    }

    public Long getOldestVaultAccessedVersion() {
        return this.oldestVaultAccessedVersion;
    }

    public void updateLatestCodeRunVersion(Long version) {
        if (version == null) {
            // Received a null version - we presumably somehow ran code that didn't have a version, and we can't be
            // sure that it should be allowed access to any sensitive data, so null out our version (which we treat as
            // meaning that we were modified infinitely far in the future).
            this.latestCodeRunVersion = null;
        } else if (this.latestCodeRunVersion != null) {
            this.latestCodeRunVersion = Math.max(this.latestCodeRunVersion, version);
        }

        validateVaultAccess();

        if (this.parentContext != null) {
            this.parentContext.updateLatestCodeRunVersion(version);
        }
    }

    public void updateOldestVaultAccessedVersion(Long version) {
        if (this.oldestVaultAccessedVersion == null) {
            this.oldestVaultAccessedVersion = version;
        } else if (version != null) {
            this.oldestVaultAccessedVersion = Math.min(this.oldestVaultAccessedVersion, version);
        }
    }

    private void validateVaultAccess() {
        // If they're both null, we're fine. If we accessed a vault but we don't have a code version, blow up (we're
        // probably in ~exec, or someone forgot to give us a version - both mean we could be doing things we shouldn't
        // be able to). If we have a code version but we didn't access a vault, we're fine. If we have both, blow up if
        // oldestVaultAccessedVersion is less than latestCodeRunVersion. (TODO: They shouldn't ever be equal, but in
        // case they are, what should we be doing? Blowing up with a message that this shouldn't have happened?)
        if (this.oldestVaultAccessedVersion == null) {
            // No vaults accessed, so we're fine
            return;
        } else if (this.latestCodeRunVersion == null) {
            // Vaults accessed but no code version. Blow up.
            throw new FactoidException("Attempted to access a vault reset at version " + this.oldestVaultAccessedVersion
                    + " from code without a version (maybe ~exec), which isn't allowed to access vaults at all");
        } else if (this.oldestVaultAccessedVersion < this.latestCodeRunVersion) {
            // Vaults that haven't been reset since this code was written were accessed. Blow up.
            throw new FactoidException("Attempted to access a vault reset at version " + this.oldestVaultAccessedVersion
                    + " from code written at version " + this.latestCodeRunVersion + ". The vault will need to be reset"
                    + " before this code will be allowed to access its contents.");
        }
    }
}
