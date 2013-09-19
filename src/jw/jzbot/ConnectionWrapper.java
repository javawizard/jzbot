package jw.jzbot;

import jw.jzbot.protocols.Connection;
import jw.jzbot.storage.Server;

/**
 * A wrapper around a Connection. Almost all of JZBot accesses connection objects through
 * a ConnectionWrapper. A connection wrapper primarily takes care of logging events sent
 * to the connection. Events received from a connection, however, are logged by the
 * connection context instead of by the wrapper.
 * 
 * @author Alexander Boyd
 * 
 */
public class ConnectionWrapper
{
    private ConnectionContext context;
    
    public ConnectionWrapper(ConnectionContext context)
    {
        this.context = context;
    }
    
    public ConnectionContext getContext()
    {
        return context;
    }
    
    public void kick(String channel, String nick, String reason)
    {
        JZBot.logEvent(context.getServerName(), channel, "kick", context.getConnection()
                .getNick(), nick + " " + reason);
        context.getConnection().kick(channel, nick, reason);
    }
    
    public void partChannel(String channel, String reason)
    {
        JZBot.logEvent(context.getServerName(), channel, "left", context.getConnection()
                .getNick(), "Left the channel: " + reason);
        context.getConnection().partChannel(channel, reason);
    }
    
    public void partChannel(String channel)
    {
        JZBot.logEvent(context.getServerName(), channel, "left", context.getConnection()
                .getNick(), "Left the channel");
        context.getConnection().partChannel(channel,
                JZBot.getDefaultPartMessage(context.getServerName(), channel));
    }
    
    public void sendAction(String target, String action)
    {
        if (target.startsWith("#"))
            JZBot.logEvent(getServerName(), target, "action", getNick(), action);
        getConnection().sendAction(target, action);
    }
    
    public void sendMessage(String target, String message)
    {
        if (target.startsWith("#"))
            JZBot.logEvent(getServerName(), target, "message", getNick(), message);
        getConnection().sendMessage(target, message);
    }
    
    public void sendNotice(String target, String message)
    {
        if (target.startsWith("#"))
            JZBot.logEvent(getServerName(), target, "notice", getNick(), message);
        getConnection().sendNotice(target, message);
    }
    
    public void setMode(String channel, String mode)
    {
        // FIXME: log this
        getConnection().setMode(channel, mode);
    }
    
    public void setTopic(String channel, String topic)
    {
        // FIXME: log this
        getConnection().setTopic(channel, topic);
    }
    
    String getNick()
    {
        return getConnection().getNick();
    }
    
    private String getServerName()
    {
        return context.getServerName();
    }
    
    public Connection getConnection()
    {
        return context.getConnection();
    }
    
    public void joinChannel(String name)
    {
        // FIXME: log this
        getConnection().joinChannel(name);
    }
    
    public String getProtocolName()
    {
        return context.getDatastoreServer().getProtocol();
    }
    
    public Server getDatastoreServer()
    {
        return context.getDatastoreServer();
    }
}
