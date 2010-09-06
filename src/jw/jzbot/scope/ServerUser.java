package jw.jzbot.scope;

import jw.jzbot.ConnectionWrapper;
import jw.jzbot.JZBot;
import jw.jzbot.utils.Utils;

public class ServerUser implements User
{
    protected String serverName;
    private String nick;
    
    public String getServerName()
    {
        return serverName;
    }
    
    public String getNick()
    {
        return nick;
    }
    
    ServerUser(String serverName, String nick)
    {
        this.serverName = serverName;
        this.nick = nick;
    }
    
    public String nick()
    {
        return getNick();
    }
    
    @Override
    public void sendMessage(String message)
    {
        ConnectionWrapper con = JZBot.getServer(serverName);
        con.sendMessage(nick, message);
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
        JZBot.getConnection(serverName).sendAction(nick, action);
    }
    
    @Override
    public void sendSpaced(String message)
    {
        Utils.sendSpaced(this, message);
    }
    
    @Override
    public String getScopeName()
    {
        return "@" + serverName;
    }
    
    @Override
    public String getCanonicalName()
    {
        return getScopeName() + "!" + nick;
    }
    
    @Override
    public boolean isSuperop()
    {
        throw new IllegalStateException("Instances of ServerUser do not "
            + "contain hostname information and so cannot check to see "
            + "if the user is a superop or not. Only instances of "
            + "UserMessenger can indicate whether or not they are a superop.");
    }
    
    @Override
    public void verifySuperop()
    {
        isSuperop();
    }
    
    @Override
    public String getHostname()
    {
        throw new IllegalStateException("Instances of ServerUser do not "
            + "track the user's hostname. Only instances of UserMessenger "
            + "track this information.");
    }
    
    @Override
    public String getUsername()
    {
        throw new IllegalStateException("Instances of ServerUser do not "
            + "track the user's username. Only instances of UserMessenger "
            + "track this information.");
    }
    
    public String toString()
    {
        return "<ServerUser: " + getCanonicalName() + ">";
    }
}
