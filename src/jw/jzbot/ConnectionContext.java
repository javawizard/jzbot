package jw.jzbot;

import jw.jzbot.protocols.Connection;
import jw.jzbot.storage.Server;

public class ConnectionContext
{
    private Server datastoreServer;
    private Connection connection;
    private String serverName;
    
    public String getServerName()
    {
        return serverName;
    }
    
    public void setServerName(String serverName)
    {
        this.serverName = serverName;
    }
    
    public Connection getConnection()
    {
        return connection;
    }
    
    public void setConnection(Connection connection)
    {
        this.connection = connection;
    }
    
    public Server getDatastoreServer()
    {
        return datastoreServer;
    }
    
    public void setDatastoreServer(Server datastoreServer)
    {
        this.datastoreServer = datastoreServer;
    }
    
    public String getServer()
    {
        return datastoreServer.getServer();
    }
    
    public int getPort()
    {
        return datastoreServer.getPort();
    }
    
    public String getNick()
    {
        return datastoreServer.getNick();
    }
    
    public String getPassword()
    {
        return datastoreServer.getPassword();
    }
    
    public void onAction(String sender, String login, String hostname, String target,
            String action)
    {
        JZBot
                .onAction(datastoreServer, serverName, sender, login, hostname, target,
                        action);
    }
    
    public void onConnect()
    {
        JZBot.onConnect(datastoreServer, serverName);
    }
    
    public void onDisconnect()
    {
        JZBot.onDisconnect(datastoreServer, serverName);
    }
    
    public void onInvitation(String targetUser, String sender, String login,
            String hostname, String toChannel)
    {
        JZBot.onInvitation(datastoreServer, serverName, targetUser, sender, login,
                hostname, toChannel);
    }
    
    public void onJoin(String channel, String sender, String login, String hostname)
    {
        JZBot.onJoin(datastoreServer, serverName, channel, sender, login, hostname);
    }
    
    public void onKick(String channel, String kickerNick, String kickerLogin,
            String kickerHostname, String recipientNick, String reason)
    {
        JZBot.onKick(datastoreServer, serverName, channel, kickerNick, kickerLogin,
                kickerHostname, recipientNick, reason);
    }
    
    public void onMessage(String channel, String sender, String login, String hostname,
            String message)
    {
        JZBot.onMessage(datastoreServer, serverName, channel, sender, login, hostname,
                message);
    }
    
    public void onNotice(String sourceNick, String sourceLogin, String sourceHostname,
            String target, String line)
    {
        JZBot.onNotice(datastoreServer, serverName, sourceNick, sourceLogin,
                sourceHostname, target, line);
    }
    
    public void onMode(String channel, String sourceNick, String sourceLogin,
            String sourceHostname, String mode)
    {
        JZBot.onMode(datastoreServer, serverName, channel, sourceNick, sourceLogin,
                sourceHostname, mode);
    }
    
    public void onNickChange(String oldNick, String login, String hostname, String newNick)
    {
        JZBot.onNickChange(datastoreServer, serverName, oldNick, login, hostname, newNick);
    }
    
    public void onPart(String channel, String sender, String login, String hostname)
    {
        // TODO: change this to pass along the reason that the user parted for
        JZBot.onPart(datastoreServer, serverName, channel, sender, login, hostname);
    }
    
    public void onPrivateMessage(String sender, String login, String hostname,
            String message)
    {
        JZBot.onPrivateMessage(datastoreServer, serverName, sender, login, hostname,
                message);
    }
    
    public void onBeforeQuit(String sourceNick, String sourceLogin, String sourceHostname,
            String reason)
    {
        JZBot.onBeforeQuit(datastoreServer, serverName, sourceNick, sourceLogin,
                sourceHostname, reason);
    }
    
    public void onTopic(String channel, String topic, String setBy, String setByUsername,
            String setByHostname, long date, boolean changed)
    {
        JZBot.onTopic(datastoreServer, serverName, channel, topic, setBy, setByUsername,
                setByHostname, date, changed);
    }
    
    private boolean hasConnected = false;
    private boolean discardNeeded = false;
    
    public void markDiscardNeeded()
    {
        discardNeeded = true;
    }
    
    public void markConnected()
    {
        hasConnected = true;
    }
    
    public boolean hasConnected()
    {
        return hasConnected;
    }
    
    public boolean discardNeeded()
    {
        return discardNeeded;
    }
    
}
