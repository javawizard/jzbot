package jw.jzbot.protocols;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import jw.jzbot.*;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.User;

/**
 * A protocol implementation that implements the BZFlag (http://bzflag.org) protocol.
 * Specifically, it implements the protocol used by BZFlag 2.0, and currently it won't
 * connect to BZFlag 3.0 servers. I'll hopefully change that soon.
 * 
 * Since a "chat room" in BZFlag speak is an entire server, the BZFlag protocol implements
 * a "channel" as a connection to a server. This has the disadvantage that at least one
 * room must be manually entered into the database in order for anyone to communicate with
 * the bot. Furthermore, each server actually has several "channels": one for global chat,
 * one for each team, and one for admin chat. Unless the bot is an administrator, it will
 * only be able to send messages to the "admin channel", not receive them; therefore, the
 * only way to get the bot to leave the admin channel if the bot is not an admin is by pm.
 * 
 * The rules of pm messages are also slightly modified: If a pm does not start with the
 * name of a channel, then it has the name of the server's global channel appended to it.
 * This allows at least somewhat predictable behavoir with relation to pms.
 * 
 * Channel names also follow a specific naming format. The name of a channel should follow
 * this general format: "#<server-hostname>:<server-port>/<room>". <server-hostname> is
 * the hostname of the server that is to be connected to. <server-port> is the port number
 * to connect to, which currently is required (IE you can't omit it). <room> is one of
 * global, rogue, red, green, blue, purple, observer, or admin. Currently, a connection
 * will not be made to the server unless the bot is set to join at least the server's
 * global channel.
 * 
 * If a server forces the client to disconnect, or if the connection to the server is lost
 * for some reason, the protocol acts as if the user had been kicked by the nickname
 * "SERVER_DISCONNECT", and the kick message will contain information on why the bot was
 * disconnected. If the bot is kicked off of the server, the protocol acts as if the user
 * had been kicked by the username of the user that performed the kick, and the kick
 * message will start with "kick: ". If the bot is banned from the server, the protocol
 * acts as if the user had been kicked by the username of the user that performed the ban,
 * and the kick message will start with "ban: ". If the bot tries to join the server, but
 * it has previously been banned, it will act as if it successfully joined the server and
 * then was kicked exactly 15 seconds later, with a kick message starting with "banned: ".
 * 
 * Usernames are *always* the same as nicknames, even if the user is not authenticated.
 * Hostnames, however, are different. A user's hostname always ends with the user's
 * username, but prefixed with one of "identified", "unidentified", "unregistered", or
 * "server", followed by a forward slash. For example, if the user javawizard2539 logs in
 * successfully, then his hostname would be "identified/javawizard2539". Messages sent
 * from the server show up as from the hostname "server/SERVER". In the future, I might
 * add special hostname prefixes to server administrators or operators.
 * 
 * If the bot is an administrator at the server, then the bot can kick and ban users.
 * Kicking is done the same was as it is in IRC, with one difference: the nickname to kick
 * can also be the slot id of the player. Bans work differently, however. Normal +b bans
 * result in a corresponding BZFlag server ban, with a description of "(mode +b ban)" and
 * a duration of 5 hours. However, the typical way to ban is to kick, but with a special
 * kick message. If a kick message starts with "+b ", then the rest of the message should
 * be of the format "+b <duration> <reason>", which will ban the recipient of the kick for
 * <duration>, with reason <reason>.
 * 
 * @author Alexander Boyd
 * 
 */
public class BZFlagProtocol implements Connection
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
        // TODO Auto-generated method stub
        return 0;
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
    
}
