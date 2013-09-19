package jw.jzbot.protocols;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import jw.jzbot.ConnectionContext;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Sink;

import org.jibble.pircbot.User;
import org.jibble.pircbot.IrcException;

/**
 * Represents a protocol that the bot can use to connect. Almost all of the methods mirror
 * their equivalents on the PircBot class, as the bot is primarily intended to be an IRC
 * bot. However, this interface exists so that you can get the bot to connect to another
 * protocol without having to modify and recompile the bot's source code. To add a new
 * protocol, you implement this class, create an implementation of {@link Protocol} that
 * returns your implementation of this interface, and register it with
 * {@link ProtocolManager#installProtocol(Protocol)}. The usual way to do this is to write
 * a plugin that does it for you.
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
    
    public void sendInvite(String nick, String channel);
    
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
    
    /**
     * Kicks the specified user off of the specified channel, if the user has priviliges
     * needed to kick the user.
     * 
     * @param channel
     *            The channel to kick the user from
     * @param user
     *            The nickname of the user to kick
     * @param reason
     *            The reason that the user is being kicked, which could be the empty
     *            string
     */
    public void kick(String channel, String user, String reason);
    
    /**
     * Sets a particular mode string at a channel. The specified mode may contain several
     * actual mode strings; for example, to give operator privileges to the user with
     * nickname "jcp" at the channel "#test", this method could be called with the
     * arguments "#test", "+o jcp". Protocols other than IRC should interpret the mode
     * string and translate it in some manner to the protocol's concepts.
     * 
     * @param channel
     * @param mode
     */
    public void setMode(String channel, String mode);
    
    /**
     * Returns true if this protocol is currently connected to the server, false if it is
     * not.
     * 
     * @return
     */
    public boolean isConnected();
    
    // TODO: consider adding getActualServer() which, for example, when
    // connecting to
    // irc.freenode.net might return "brown.freenode.net"
    /**
     * Instructs the protocol to leave a particular channel. This can be asynchronous; IE
     * it doesn't need to cause the channel list to be updated immediately.
     */
    public void partChannel(String channel, String reason);
    
    /**
     * Instructs this protocol to disconnect from the server it is connected to. If the
     * server supports a disconnect reason, then the supplied reason should be used.
     * 
     * @param message
     */
    public void disconnect(String message);
    
    /**
     * If this protocol supports message delaying, then this returns the number of
     * messages that have not yet been sent because of the enforced delay.
     * 
     * @return
     */
    public int getOutgoingQueueSize();
    
    /**
     * Sets the topic for the specified channel. This should do nothing if the bot doesn't
     * have privileges to set the topic on a server.
     * 
     * @param channel
     *            The channel to set the topic on
     * @param topic
     *            The topic to set
     */
    public void setTopic(String channel, String topic);
    
    /**
     * Changes the bot's nick to the new nickname specified. This generally should not
     * persist through a reconnect. If this protocol does not support nickname changing,
     * this should do nothing.
     * 
     * @param newnick
     *            The new nickname
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
    
    /**
     * Returns true if this protocol is one that prefers long responses to be pastebinned,
     * false if it would prefer some other means, such as multiple messages, to be used.
     * For example, IRC prefers pastebin posts, but BZFlag does not as it's quite
     * difficult to copy a URL from within a BZFlag client and view it.
     * 
     * @return
     */
    public boolean likesPastebin();
}
