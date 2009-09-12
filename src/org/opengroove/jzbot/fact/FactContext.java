package org.opengroove.jzbot.fact;

import java.util.Map;

public class FactContext
{
    private Map<String, String> localVars;
    private Map<String, String> globalVars;
    
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
