package jw.jzbot;

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
    
    private String getNick()
    {
        return getConnection().getNick();
    }
    
    private String getServerName()
    {
        return context.getServerName();
    }
    
    private Connection getConnection()
    {
        return context.getConnection();
    }
}
