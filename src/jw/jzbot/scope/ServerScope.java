package jw.jzbot.scope;

public class ServerScope implements Scope
{
    private String server;
    
    ServerScope(String server)
    {
        this.server = server;
    }
    
    @Override
    public String getChannelName()
    {
        return null;
    }
    
    @Override
    public String getServerName()
    {
        return server;
    }
    
    @Override
    public String getScopeName()
    {
        return "@" + server;
    }
    
    @Override
    public String getCanonicalName()
    {
        return getScopeName();
    }
    
}
