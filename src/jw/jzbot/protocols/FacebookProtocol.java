package jw.jzbot.protocols;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.User;

import jw.jzbot.Connection;
import jw.jzbot.ConnectionContext;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Sink;

public class FacebookProtocol implements Connection
{
    
    @Override
    public void changeNick(String newnick)
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void connect() throws IOException, IrcException
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void disconnect(String message)
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public String[] getChannels()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public String getNick()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public int getOutgoingQueueSize()
    {
        // TODO Auto-generated method stub
        return 0;
    }
    
    @Override
    public int getProtocolDelimitedLength()
    {
        // TODO: figure out what the maximum length of a wall post or comment or w/e is.
        return 300;
    }
    
    @Override
    public User[] getUsers(String channel)
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public void init(ConnectionContext context)
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public boolean isConnected()
    {
        // TODO Auto-generated method stub
        return false;
    }
    
    @Override
    public void joinChannel(String channel)
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void kick(String channel, String user, String reason)
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void partChannel(String channel, String reason)
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void sendAction(String target, String message)
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void sendMessage(String target, String message)
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void sendNotice(String target, String message)
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void setEncoding(String string) throws UnsupportedEncodingException
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void setLogin(String nick)
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void setMessageDelay(long ms)
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void setMode(String channel, String mode)
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void setName(String nick)
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void setTopic(String channel, String topic)
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void setVersion(String string)
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public boolean supportsMessageDelay()
    {
        // TODO Auto-generated method stub
        return false;
    }
    
    @Override
    public void discard()
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void processProtocolFunction(Sink sink, ArgumentList arguments,
            FactContext context)
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public boolean likesPastebin()
    {
        return true;
    }
    
    @Override
    public void sendInvite(String nick, String channel)
    {
        // TODO Auto-generated method stub
        
    }
    
}
