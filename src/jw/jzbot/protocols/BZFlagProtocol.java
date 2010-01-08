package jw.jzbot.protocols;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import jw.jzbot.*;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Sink;
import jw.jzbot.protocols.bzflag.Message;
import jw.jzbot.protocols.bzflag.ServerLink;
import jw.jzbot.protocols.bzflag.pack.MsgAccept;
import jw.jzbot.protocols.bzflag.pack.MsgAddPlayer;
import jw.jzbot.protocols.bzflag.pack.MsgEnter;
import jw.jzbot.protocols.bzflag.pack.MsgGameTime;
import jw.jzbot.protocols.bzflag.pack.MsgLagPing;
import jw.jzbot.protocols.bzflag.pack.MsgMessage;
import jw.jzbot.protocols.bzflag.pack.MsgPlayerInfo;
import jw.jzbot.protocols.bzflag.pack.MsgReject;
import jw.jzbot.protocols.bzflag.pack.MsgRemovePlayer;
import jw.jzbot.protocols.bzflag.pack.MsgSetVar;
import jw.jzbot.protocols.bzflag.pack.MsgSuperKill;
import jw.jzbot.protocols.bzflag.pack.MsgTeamUpdate;

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
    private boolean hasShutdown = false;
    
    private boolean joinedAll = false;
    private boolean joinedTeam = false;
    private boolean joinedAdmin = false;
    
    /*
     * Doesn't particularly matter what type of message this is
     */
    public static final Message HALT_QUEUE_MESSAGE = new MsgSuperKill();
    public static final Object CONNECTION_SUCCESSFUL = new Object();
    private static final int OBSERVER = 5;
    
    private ServerLink serverLink;
    
    private Map<String, String> serverVariables = new HashMap<String, String>();
    
    private BlockingQueue<Message> outQueue = new LinkedBlockingQueue<Message>(800);
    
    private BlockingQueue<Message> deferredQueue = new LinkedBlockingQueue<Message>(800);
    
    private BlockingQueue<Message> immediateQueue = new LinkedBlockingQueue<Message>(800);
    
    private BlockingQueue<Object> initialConnectQueue = new LinkedBlockingQueue<Object>(50);
    
    private OutputThread outputThread;
    
    private InputThread inputThread;
    
    private DispatchThread immediateDispatcher;
    
    private DispatchThread deferredDispatcher;
    
    private class DispatchThread extends Thread
    {
        private BlockingQueue<Message> dispatchQueue;
        
        private boolean running = true;
        
        public DispatchThread(BlockingQueue<Message> queue)
        {
            this.dispatchQueue = queue;
        }
        
        public void shutdown()
        {
            running = false;
            dispatchQueue.offer(HALT_QUEUE_MESSAGE);
        }
        
        public void run()
        {
            while (running)// TODO: this blocks sync'ing of messages after a disconnect.
            // Should we do this, or should we just wait for HALT_QUEUE_MESSAGE to arrive?
            // If we do that, we'll have to make sure we don't ever forget to send
            // HALT_QUEUE_MESSAGE since that could cause the thread to essentially never
            // stop running until a restart. We could also do something where we exit when
            // we're not running and the queue has become empty.
            {
                Message message;
                try
                {
                    message = dispatchQueue.poll(30, TimeUnit.SECONDS);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                    continue;
                }
                if (message == null)
                    continue;
                if (message == HALT_QUEUE_MESSAGE)
                    return;
                dispatch(message);
            }
        }
    }
    
    private class InputThread extends Thread
    {
        private volatile boolean running;
        
        public void shutdown()
        {
            running = false;
        }
        
        public void run()
        {
            while (running)
            {
                try
                {
                    Message message = serverLink.receive();
                    syncToDispatch(message);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    shutdownOnError(e);
                    return;
                }
            }
        }
    }
    
    private class OutputThread extends Thread
    {
        private volatile boolean running;
        
        public void shutdown()
        {
            running = false;
        }
        
        public void run()
        {
            while (running)
            {
                Message message;
                try
                {
                    message = outQueue.poll(30, TimeUnit.SECONDS);
                    if (message == null)
                        continue;
                    serverLink.send(message);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    shutdownOnError(e);
                    return;
                }
            }
        }
    }
    
    public static class Player implements User
    {
        public int playerId;
        public String callsign;
        public String email;
        public int team;
        public int wins;
        public int losses;
        public int tks;
        public boolean admin;
        public boolean registered;
        public boolean verified;
        
        @Override
        public String getNick()
        {
            return callsign;
        }
        
        @Override
        public boolean hasVoice()
        {
            return registered && verified;
        }
        
        @Override
        public boolean isAdmin()
        {
            return false;
        }
        
        @Override
        public boolean isFounder()
        {
            // TODO: if we can get this information from the server, it might be cool to
            // have this be true when the user has authenticated with the server by use of
            // the server password. It's not critical or anything, though, since
            // authenticating via a server password is a fairly rare event nowadays.
            return false;
        }
        
        @Override
        public boolean isHalfop()
        {
            return false;
        }
        
        @Override
        public boolean isOp()
        {
            return verified;
        }
        
    }
    
    private Player[] players = new Player[256];
    private ConnectionContext context;
    private Socket socket;
    
    public void syncToDispatch(Message message)
    {
        if (message instanceof MsgLagPing)
            immediateQueue.offer(message);
        else
            deferredQueue.offer(message);
    }
    
    public synchronized void shutdownOnError(Exception e)
    {
        if (hasShutdown)
            return;
        doShutdown();
        initialConnectQueue.offer(e);
    }
    
    private void doShutdown()
    {
        serverLink.closeIgnore();
        try
        {
            inputThread.shutdown();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        try
        {
            outputThread.shutdown();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        try
        {
            deferredDispatcher.shutdown();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        try
        {
            immediateDispatcher.shutdown();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
    
    public void dispatch(Message message)
    {
        if (message instanceof MsgLagPing)
        {
            // Send the message right back to the server
            outQueue.offer(message);
        }
        else if (message instanceof MsgAccept)
        {
            // We'll ignore this for now. We're going to rely on receiving a MsgAdd with
            // our id in it to signal that we got accepted.
        }
        else if (message instanceof MsgAddPlayer)
        {
            
        }
        else if (message instanceof MsgGameTime)
        {
            
        }
        else if (message instanceof MsgMessage)
        {
            
        }
        else if (message instanceof MsgPlayerInfo)
        {
            
        }
        else if (message instanceof MsgReject)
        {
            
        }
        else if (message instanceof MsgRemovePlayer)
        {
            
        }
        else if (message instanceof MsgSetVar)
        {
            
        }
        else if (message instanceof MsgSuperKill)
        {
            
        }
        else if (message instanceof MsgTeamUpdate)
        {
            
        }
    }
    
    @Override
    public void changeNick(String newnick)
    {
        // TODO: we're doing nothing for now, since bzflag doesn't support switching
        // nicks. Should we throw an exception instead? (or for that matter, we could
        // quickly disconnect and re-connect, althought tha t wouldn't be quite as
        // transparent)
    }
    
    @Override
    public void connect() throws IOException, IrcException
    {
        long startTime = System.currentTimeMillis();
        socket = new Socket();
        socket.setSoTimeout(30 * 1000);
        String host = context.getServer();
        int port = context.getPort();
        socket.connect(new InetSocketAddress(host, port), 30 * 1000);
        serverLink = new ServerLink(socket, true);
        initThreads();
    }
    
    private void initThreads()
    {
        inputThread = new InputThread();
        outputThread = new OutputThread();
        immediateDispatcher = new DispatchThread(immediateQueue);
        deferredDispatcher = new DispatchThread(deferredQueue);
        inputThread.start();
        outputThread.start();
        immediateDispatcher.start();
        deferredDispatcher.start();
    }
    
    @Override
    public void disconnect(String message)
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public String[] getChannels()
    {
        ArrayList<String> list = new ArrayList<String>();
        if (joinedAll)
            list.add("#all");
        if (joinedAdmin)
            list.add("#admin");
        if (joinedTeam)
            list.add("#team");
        return list.toArray(new String[0]);
    }
    
    @Override
    public String getNick()
    {
        // BZFlag nicknames can't be changed once connected to the server, and we're not
        // including any logic for autoswitching nicks if the requested nick is in use, so
        // we just need to look up our player name and return it.
        return players[serverLink.getLocalId()].callsign;
    }
    
    @Override
    public int getOutgoingQueueSize()
    {
        return 0;
    }
    
    @Override
    public int getProtocolDelimitedLength()
    {
        // This could probably be increased to 127, but I'm airing more on the side of
        // absolute paranoid caution
        return 112;
    }
    
    @Override
    public User[] getUsers(String channel)
    {
        boolean checkAll = channel.equals("#all");
        boolean checkAdmin = channel.equals("#admin");
        boolean checkTeam = channel.equals("#team");
        if ((checkAll && joinedAll) || (checkAdmin && joinedAdmin)
                || (checkTeam && joinedTeam))
        {
            ArrayList<User> list = new ArrayList<User>();
            for (Player player : players)
            {
                if (player == null)
                    continue;
                if (checkAll)
                    list.add(player);
                else if (checkAdmin && player.admin)
                    list.add(player);
                else if (checkTeam && player.team == OBSERVER)
                    list.add(player);
            }
            return list.toArray(new User[0]);
        }
        return null;
    }
    
    @Override
    public void init(ConnectionContext context)
    {
        this.context = context;
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
        return false;
    }
    
    @Override
    public void discard()
    {
    }
    
    @Override
    public void processProtocolFunction(Sink sink, ArgumentList arguments,
            FactContext context)
    {
        throw new UnsupportedOperationException("There aren't yet any "
                + "protocol-specific functions for the BZFlag protocol.");
    }
    
}
