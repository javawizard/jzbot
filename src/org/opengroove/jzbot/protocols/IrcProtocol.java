package org.opengroove.jzbot.protocols;

import org.opengroove.jzbot.Protocol;
import org.opengroove.jzbot.JZBot;
import org.jibble.pircbot.PircBot;

public class IrcProtocol extends PircBot implements Protocol
{
    public IrcProtocol()
    {
        super();
    }
    
    @Override
    public int getProtocolDelimitedLength()
    {
        return 400;
    }
    
    @Override
    public void init()
    {
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
    protected void onJoin(String channel, String sender, String login,
            String hostname)
    {
        JZBot.onJoin(channel, sender, login, hostname);
    }
    
    @Override
    protected void onKick(String channel, String kickerNick,
            String kickerLogin, String kickerHostname, String recipientNick,
            String reason)
    {
        JZBot.onKick(channel, kickerNick, kickerLogin, kickerHostname,
                recipientNick, reason);
    }
    
    @Override
    protected void onMessage(String channel, String sender, String login,
            String hostname, String message)
    {
        JZBot.onMessage(channel, sender, login, hostname, message);
    }
    
    @Override
    protected void onMode(String channel, String sourceNick,
            String sourceLogin, String sourceHostname, String mode)
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
    protected void onPart(String channel, String sender, String login,
            String hostname)
    {
        JZBot.onPart(channel, sender, login, hostname);
    }
    
    @Override
    protected void onPrivateMessage(String sender, String login,
            String hostname, String message)
    {
        JZBot.onPrivateMessage(sender, login, hostname, message);
    }
    
    @Override
    protected void onQuit(String sourceNick, String sourceLogin,
            String sourceHostname, String reason)
    {
        JZBot.onQuit(sourceNick, sourceLogin, sourceHostname, reason);
    }
    
    @Override
    protected void onTopic(String channel, String topic, String setBy,
            long date, boolean changed)
    {
        JZBot.onTopic(channel, topic, setBy, date, changed);
    }
    
}
