package jw.jzbot.fact;

import java.util.HashMap;
import java.util.Map;

import jw.jzbot.JZBot;
import jw.jzbot.ServerUser;
import jw.jzbot.fact.debug.DebugInstance;
import jw.jzbot.fact.debug.DebugSupport;

import org.jdom.Document;

public class FactContext
{
    private Map<String, String> localVars = new HashMap<String, String>();
    private Map<String, String> globalVars = JZBot.globalVariables;
    private Map<String, Document> xmlDocuments = new HashMap<String, Document>();
    // TODO: replace xmlDocuments with objectStorage. objectStorage is a map that groups
    // of functions can use to register custom storage objects, where the name should be
    // <group>-<whatever>. For example, xml documents will be stored as "xml-<name>",
    // where <name> is the name of the xml document.
    private Map<String, Object> objectStorage = new HashMap<String, Object>();
    private Map<String, FactEntity> storedSubroutines = new HashMap<String, FactEntity>();
    
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
    
    public String getCheckedServer()
    {
        if (server == null)
            throw new FactoidException("A server is needed, but one was not specified.");
        return server;
    }
    
}
