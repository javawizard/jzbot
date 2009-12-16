package jw.jzbot.protocols;

import java.io.IOException;

import jw.jzbot.ConnectionContext;
import jw.jzbot.JZBot;
import jw.jzbot.Connection;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.PircBot;

public class IrcProtocol extends PircBot implements Connection
{
    @Override
    public void changeNick(String newNick)
    {
        super.changeNick(newNick);
    }
    
    public void disconnect(String message)
    {
        super.quitServer(message);
    }
    
    @Override
    public void kick(String channel, String nick, String reason)
    {
        JZBot.logEvent(channel, "kick", getNick(), nick + " " + reason);
        super.kick(channel, nick, reason);
    }
    
    @Override
    protected void onAction(String sender, String login, String hostname, String target,
            String action)
    {
        JZBot.onAction(sender, login, hostname, target, action);
    }
    
    @Override
    public void partChannel(String channel, String reason)
    {
        JZBot.logEvent(channel, "left", getNick(), "Left the channel: " + reason);
        super.partChannel(channel, reason);
    }
    
    @Override
    public void partChannel(String channel)
    {
        JZBot.logEvent(channel, "left", getNick(), "Left the channel");
        super.partChannel(channel);
    }
    
    @Override
    public void quitServer(String reason)
    {
        super.quitServer(reason);
    }
    
    @Override
    public void sendAction(String target, String action)
    {
        if (target.startsWith("#"))
            JZBot.logEvent(target, "action", getNick(), action);
        super.sendAction(target, action);
    }
    
    @Override
    public void sendMessage(String target, String message)
    {
        if (target.startsWith("#"))
            JZBot.logEvent(target, "message", getNick(), message);
        super.sendMessage(target, message);
    }
    
    @Override
    public void sendNotice(String target, String message)
    {
        JZBot.logEvent(target, "notice", getNick(), message);
        super.sendNotice(target, message);
    }
    
    @Override
    public void setMode(String channel, String mode)
    {
        super.setMode(channel, mode);
    }
    
    @Override
    public void setTopic(String channel, String topic)
    {
        super.setTopic(channel, topic);
    }
    
    public IrcProtocol()
    {
        super();
    }
    
    public int getProtocolDelimitedLength()
    {
        return 400;
    }
    
    @Override
    protected void onConnect()
    {
        JZBot.onConnect();
    }
    
    @Override
    protected void onDisconnect()
    {
        JZBot.onDisconnect();
    }
    
    @Override
    protected void onJoin(String channel, String sender, String login, String hostname)
    {
        JZBot.onJoin(channel, sender, login, hostname);
    }
    
    @Override
    protected void onKick(String channel, String kickerNick, String kickerLogin,
            String kickerHostname, String recipientNick, String reason)
    {
        JZBot.onKick(channel, kickerNick, kickerLogin, kickerHostname, recipientNick,
                reason);
    }
    
    @Override
    protected void onMessage(String channel, String sender, String login, String hostname,
            String message)
    {
        JZBot.onMessage(channel, sender, login, hostname, message);
    }
    
    @Override
    protected void onNotice(String sourceNick, String sourceLogin, String sourceHostname,
            String target, String line)
    {
        JZBot.onNotice(sourceNick, sourceLogin, sourceHostname, target, line);
    }
    
    @Override
    protected void onMode(String channel, String sourceNick, String sourceLogin,
            String sourceHostname, String mode)
    {
        JZBot.onMode(channel, sourceNick, sourceLogin, sourceHostname, mode);
    }
    
    @Override
    protected void onNickChange(String oldNick, String login, String hostname,
            String newNick)
    {
        JZBot.onNickChange(oldNick, login, hostname, newNick);
    }
    
    @Override
    protected void onPart(String channel, String sender, String login, String hostname)
    {
        JZBot.onPart(channel, sender, login, hostname);
    }
    
    @Override
    protected void onPrivateMessage(String sender, String login, String hostname,
            String message)
    {
        JZBot.onPrivateMessage(sender, login, hostname, message);
    }
    
    @Override
    protected void onBeforeQuit(String sourceNick, String sourceLogin,
            String sourceHostname, String reason)
    {
        JZBot.onBeforeQuit(sourceNick, sourceLogin, sourceHostname, reason);
    }
    
    @Override
    protected void onTopic(String channel, String topic, String setBy, long date,
            boolean changed)
    {
        JZBot.onTopic(channel, topic, setBy, date, changed);
    }
    
    @Override
    public void connect() throws IOException, IrcException
    {
        setName(context.getNick());
        connect(context.getServer(), context.getPort(), context.getPassword());
    }
    
    private ConnectionContext context;
    
    @Override
    public void init(ConnectionContext context)
    {
        this.context = context;
    }
    
    @Override
    public boolean supportsMessageDelay()
    {
        return true;
    }
    
}
