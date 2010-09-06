package jw.jzbot.scope;

import jw.jzbot.ConnectionWrapper;
import jw.jzbot.JZBot;
import jw.jzbot.utils.Utils;

public class ChannelScope implements Messenger, Scope
{
    private String serverName;
    private String channel;
    
    public ChannelScope(String serverName, String channel)
    {
        super();
        this.serverName = serverName;
        this.channel = channel;
    }
    
    public String getServerName()
    {
        return serverName;
    }
    
    public String getChannelName()
    {
        return channel;
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
    public boolean likesPastebin()
    {
        return JZBot.getServer(serverName).getConnection().likesPastebin();
    }
    
    @Override
    public void sendAction(String action)
    {
        JZBot.getConnection(serverName).sendAction(channel, action);
    }
    
    @Override
    public void sendSpaced(String message)
    {
        Utils.sendSpaced(this, message);
    }
    
    @Override
    public String getScopeName()
    {
        return "@" + serverName + channel;
    }
    
    @Override
    public String getCanonicalName()
    {
        return getScopeName();
    }
    
    public String toString()
    {
        return "<ChannelScope: " + getCanonicalName() + ">";
    }
    
}
