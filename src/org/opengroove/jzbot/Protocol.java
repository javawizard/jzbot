package org.opengroove.jzbot;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.jibble.pircbot.User;
import org.jibble.pircbot.IrcException;

/**
 * Represents a protocol that the bot can use to connect. Almost all of the methods mirror
 * their equivalents on the PircBot class, as the bot is primarily intended to be an IRC
 * bot. However, this interface exists so that you can get the bot to connect to another
 * protocol without having to modify and recompile the bot's source code. You basically
 * implement this class, then edit the single row that's in the CONFIG table in the
 * database, and change the column "protocol" to be the fully-qualified class name of the
 * implementation of this interface that you want to use. The default is
 * "org.opengroove.jzbot.protocols.IrcProtocol", which essentially wraps a PircBot. There
 * is another protocol, "org.opengroove.jzbot.protocols.BZFlagProtocol", that comes with
 * JZBot that connects it to a BZFlag (http://bzflag.org) server. See that class for more
 * information about the protocol.
 * 
 * @author Alexander Boyd
 * 
 */
public interface Protocol
{
    
    public void sendAction(String target, String message);
    
    public void sendMessage(String target, String message);
    
    public void setMessageDelay(long ms);
    
    public void setLogin(String nick);
    
    public void setName(String nick);
    
    public void setVersion(String string);
    
    public void setAutoNickChange(boolean b);
    
    public void setEncoding(String string) throws UnsupportedEncodingException;
    
    public void connect(String server, int port, String password) throws IOException,
            IrcException;
    
    public String getNick();
    
    public String[] getChannels();
    
    public void joinChannel(String channel);
    
    public void reconnect() throws IOException, IrcException;
    
    public User[] getUsers(String channel);
    
    public int getProtocolDelimitedLength();
    
    public void init();
    
    public void kick(String channel, String sender, String string);
    
    public void setMode(String channel, String string);
    
    public boolean isConnected();
    
    public String getServer();
    
    public void partChannel(String channel, String string);
    
    public void disconnect(String message);
    
    public int getOutgoingQueueSize();
    
    public void setTopic(String channel, String topic);
}
