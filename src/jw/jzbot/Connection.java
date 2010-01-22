package jw.jzbot;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Sink;

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
 * "jw.jzbot.protocols.IrcProtocol", which essentially wraps a PircBot. There is another
 * protocol, "jw.jzbot.protocols.BZFlagProtocol", that comes with JZBot that connects it
 * to a BZFlag (http://bzflag.org) server. See that class for more information about the
 * protocol.
 * 
 * @author Alexander Boyd
 * 
 */
public interface Connection
{
    public void init(ConnectionContext context);
    
    public void sendAction(String target, String message);
    
    public void sendMessage(String target, String message);
    
    public void sendNotice(String target, String message);
    
    public void setMessageDelay(long ms);
    
    public boolean supportsMessageDelay();
    
    public void setLogin(String nick);
    
    public void setName(String nick);
    
    public void setVersion(String string);
    
    public void setEncoding(String string) throws UnsupportedEncodingException;
    
    public void connect() throws IOException, IrcException;
    
    public String getNick();
    
    public String[] getChannels();
    
    public void joinChannel(String channel);
    
    public User[] getUsers(String channel);
    
    /**
     * Returns an approximation of the maximum length of a message on this protocol. This
     * must be either the same as or less than the maximum size of a message that can be
     * sent with this protocol.
     * 
     * @return
     */
    public int getProtocolDelimitedLength();
    
    public void kick(String channel, String user, String reason);
    
    public void setMode(String channel, String mode);
    
    public boolean isConnected();
    
    // TODO: consider adding getActualServer() which, for example, when connecting to
    // irc.freenode.net might return "brown.freenode.net"
    
    public void partChannel(String channel, String reason);
    
    public void disconnect(String message);
    
    /**
     * If this protocol supports message delaying, then this returns the number of
     * messages that have not yet been sent because of the enforced delay.
     * 
     * @return
     */
    public int getOutgoingQueueSize();
    
    public void setTopic(String channel, String topic);
    
    /**
     * Changes the bot's nick to the new nickname specified. This generally should not
     * persist through a reconnect.
     * 
     * @param newnick
     */
    public void changeNick(String newnick);
    
    /**
     * Execute a protocol-specific function.
     * 
     * @param sink
     *            The sink to which the function's output should be written
     * @param arguments
     *            The arguments provided in the function
     * @param context
     *            The context that the function is being run under
     */
    public void processProtocolFunction(Sink sink, ArgumentList arguments,
            FactContext context);
    
    /**
     * Called once when JZBot is about to discard this connection. No other methods will
     * ever be called on this connection again. Unless some unusual circumstance occurs,
     * this will always be preceded by a call to disconnect().
     */
    public void discard();
    
    public boolean likesPastebin();
}
