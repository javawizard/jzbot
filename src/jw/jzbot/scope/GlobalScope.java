package jw.jzbot.scope;

public class GlobalScope implements Scope
{
    GlobalScope()
    {
        
    }
    
    @Override
    public String getChannelName()
    {
        return null;
    }
    
    @Override
    public String getServerName()
    {
        return null;
    }
    
    @Override
    public String getScopeName()
    {
        return "";
    }
    
    @Override
    public String getCanonicalName()
    {
        return "";
    }
    
    public String toString()
    {
        return "<GlobalScope>";
    }
}
