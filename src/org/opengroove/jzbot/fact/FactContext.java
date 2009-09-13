package org.opengroove.jzbot.fact;

import java.util.Map;

public class FactContext
{
    private Map<String, String> localVars;
    private Map<String, String> globalVars;
    private boolean action;
    private String channel;
    private String sender;
    
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
}
