package jw.jzbot;

public class ServerUser
{
    private String serverName;
    private String nick;
    private String hostname;
    
    public String getHostname()
    {
        return hostname;
    }
    
    public void setHostname(String hostname)
    {
        this.hostname = hostname;
    }
    
    public String getServerName()
    {
        return serverName;
    }
    
    public void setServerName(String serverName)
    {
        this.serverName = serverName;
    }
    
    public String getNick()
    {
        return nick;
    }
    
    public void setNick(String nick)
    {
        this.nick = nick;
    }
    
    public ServerUser(String serverName, String nick, String hostname)
    {
        super();
        this.serverName = serverName;
        this.nick = nick;
        this.hostname = hostname;
    }
    
    public boolean isSuperop()
    {
        return JZBot.isSuperop(serverName, hostname);
    }
    
    public void verifySuperop()
    {
        JZBot.verifySuperop(serverName, hostname);
    }
    
}
