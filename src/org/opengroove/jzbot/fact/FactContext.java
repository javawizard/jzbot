package org.opengroove.jzbot.fact;

import java.util.HashMap;
import java.util.Map;

public class FactContext
{
    private Map<String, String> localVars = new HashMap<String, String>();
    private Map<String, String> globalVars;
    private boolean action;
    private String channel;
    private String sender;
    private int messageCount = 0;
    private int importCount = 0;
    public static final int MAX_IMPORT_COUNT = 30;
    
    public int getImportCount()
    {
        return importCount;
    }
    
    public void setImportCount(int importCount)
    {
        this.importCount = importCount;
    }
    
    private String self;
    
    public String getSelf()
    {
        return self;
    }
    
    public void setSelf(String self)
    {
        this.self = self;
    }
    
    public static final int MAX_MESSAGE_COUNT = 6;
    
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
    
    public void setGlobalVars(Map<String, String> globalVars)
    {
        this.globalVars = globalVars;
    }
    
    public void incrementMessageCount()
    {
        messageCount += 1;
        if (messageCount > MAX_MESSAGE_COUNT)
            throw new FactoidException("Maximum limit of " + messageCount
                    + " messages per factoid invocation exceeded.");
    }
    
    public void incrementImportCount()
    {
        importCount += 1;
        if (importCount > MAX_IMPORT_COUNT)
            throw new FactoidException("Maximum limit of " + importCount
                    + " {{import}} and {{run}} calls per "
                    + "factoid invocation exceeded.");
    }
    
    public int getMessageCount()
    {
        return messageCount;
    }
    
    public void setMessageCount(int messageCount)
    {
        this.messageCount = messageCount;
    }
}
