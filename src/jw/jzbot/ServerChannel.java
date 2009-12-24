package jw.jzbot;

public class ServerChannel implements Messenger
{
    private String serverName;
    private String channel;
    
    public ServerChannel(String serverName, String channel)
    {
        super();
        this.serverName = serverName;
        this.channel = channel;
    }
    
    public String getServerName()
    {
        return serverName;
    }
    
    public void setServerName(String serverName)
    {
        this.serverName = serverName;
    }
    
    public String getChannel()
    {
        return channel;
    }
    
    public void setChannel(String channel)
    {
        this.channel = channel;
    }
    
    @Override
    public void sendMessage(String message)
    {
        ConnectionWrapper con = JZBot.getConnection(serverName);
        con.sendMessage(channel, message);
    }
    
    @Override
    public int getProtocolDelimitedLength()
    {
        return JZBot.getServer(serverName).getConnection().getProtocolDelimitedLength();
    }
    
    @Override
    public void sendAction(String action)
    {
        JZBot.getConnection(serverName).sendAction(channel, action);
    }
    
}
