package jw.jzbot.fact;

import java.util.HashMap;
import java.util.Map;

import jw.jzbot.JZBot;

import org.jdom.Document;

public class FactContext
{
    private Map<String, String> localVars = new HashMap<String, String>();
    private Map<String, String> globalVars = JZBot.globalVariables;
    private Map<String, Document> xmlDocuments = new HashMap<String, Document>();
    private boolean action;
    private String channel;
    private String sender;
    private String self = (JZBot.bot == null ? null : JZBot.bot.getNick());
    private FactQuota quota = new FactQuota();
    
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
    
    public String getSender()
    {
        return sender;
    }
    
    public void setSender(String sender)
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
    
}
