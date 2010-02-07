package jw.jzbot.protocols;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URLEncoder;
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
import jw.jzbot.protocols.bzflag.BZFlagConnector;
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
import jw.jzbot.protocols.bzflag.pack.MsgPlayerInfo.Info;
import jw.jzbot.utils.Pastebin;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
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
                try
                {
                    dispatch(message);
                }
                catch (Exception e)
                {
                    new Exception("Exception while dispatching message", e)
                            .printStackTrace();
                }
            }
        }
    }
    
    private class InputThread extends Thread
    {
        private volatile boolean running = true;
        
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
        private volatile boolean running = true;
        
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
            // We just need to update the player list accordingly.
            MsgAddPlayer m = (MsgAddPlayer) message;
            Player player = new Player();
            player.callsign = m.callsign;
            player.email = m.email;
            player.losses = m.losses;
            player.playerId = m.id;
            player.team = m.team;
            player.tks = m.tks;
            player.wins = m.wins;
            if (players[m.id] != null)
                // This shouldn't happen, so we'll log a warning if it does
                System.err.println("WARNING: overwriting player at index " + m.id);
            players[m.id] = player;
            if (m.id != serverLink.getLocalId())
            {
                // This is another player
                boolean isObserver = player.team == OBSERVER;
                String hostname = getPlayerHostname(player);
                String login = getPlayerLogin(player);
                tryJoin("#all", player.callsign, login, hostname);
                if (isObserver)
                    tryJoin("#team", player.callsign, login, hostname);
            }
            else
            {
                // This is us
                initialConnectQueue.add(CONNECTION_SUCCESSFUL);
            }
        }
        else if (message instanceof MsgGameTime)
        {
            // We're ignoring this for now. In the future, we could store this and make it
            // available as an extended function or something. Or have a standard function
            // for all protocols, and IRC's version of it asks the server for its time, or
            // maybe asks the server for its time periodically and returns the stored
            // value compensated for when it was received.
        }
        else if (message instanceof MsgMessage)
        {
            MsgMessage m = (MsgMessage) message;
            System.out.println("Message from #" + m.from + " to #" + m.to + ": "
                + m.message);
            debugDumpMessage("BZFlag inbound message: ", m.message);
            // Server messages we'll send as notices to avoid the bot trying to respond to
            // periodic messages sent out by the server.
            if (m.from == serverLink.getLocalId())
                // We're ignoring messages that appear to be from us for now.
                return;
            boolean fromServer = m.from == BZFlagConnector.MsgToServerPlayer;
            String channel = null;
            if (m.to == BZFlagConnector.MsgToAllPlayers)
            {
                if (!joinedAll)
                    return;
                channel = "#all";
            }
            else if (m.to == BZFlagConnector.MsgToAdmins)
            {
                if (!joinedAdmin)
                    return;
                channel = "#admin";
            }
            else if (m.to == BZFlagConnector.MsgToObserverTeam)
            {
                if (!joinedTeam)
                    return;
                channel = "#team";
            }
            else if (m.to != serverLink.getLocalId())
                // Ignore messages sent from someone else to someone else. There's no
                // reason we should receive this, but just in case...
                return;
            // If this was a pm, channel will be null at this point. Either way, we need
            // to figure out the name of the user that sent the message.
            String from = null;
            if (fromServer)
                from = "SERVER";
            else
            {
                for (Player p : players)
                {
                    if (p != null)
                    {
                        if (p.playerId == m.from)
                        {
                            from = p.callsign;
                            break;
                        }
                    }
                }
            }
            if (from == null)
                // For some reason, we don't have the player's name. This is where it
                // shows up as "UNKNOWN" in the BZFlag client, but we'll just discard the
                // message.
                return;
            // We can now dispatch the message.
            if (fromServer)
            {
                System.out.println("Server message: " + m.message);
                if (getLocalPlayer() == null)// Server message before we get added to the
                    // player list
                    return;
                context.onNotice("SERVER", "SERVER", "SERVER", channel != null ? channel
                        : getLocalPlayer().callsign, m.message);
            }
            else
            {
                if (isActionMessage(m.message, from))
                {
                    String actual = getRawAction(m.message, from);
                    context.onAction(from, getPlayerLogin(from), getPlayerHostname(from),
                            channel == null ? from : channel, actual);
                }
                else
                {
                    if (channel == null)
                        context.onPrivateMessage(from, getPlayerLogin(from),
                                getPlayerHostname(from), m.message);
                    else
                        context.onMessage(channel, from, getPlayerLogin(from),
                                getPlayerHostname(from), m.message);
                }
            }
        }
        else if (message instanceof MsgPlayerInfo)
        {
            /*
             * This one is tricky. We need to get the previous status of the player and
             * compare it with the new status. Specifically:
             * 
             * If the player is now an admin and was not before, they receive mode +o at
             * #all, and they join #admin and receive mode +o there
             * 
             * If the player is no longer an admin but was before, they receive mode -o at
             * #all, and they part #admin
             * 
             * If the player is verified and was not before, they receive mode +v at #all.
             * They do not receive any such flag at #admin if they happen to be joined
             * there.
             * 
             * If the player is no longer verified but was before, they receive mode -v at
             * #all. They do not receive any such flag change at #admin if they happen to
             * be joined there.
             */
            MsgPlayerInfo m = (MsgPlayerInfo) message;
            for (Info info : m.info)
            {
                Player player = players[info.playerId];
                boolean isUs = info.playerId == serverLink.getLocalId();
                boolean isOther = !isUs;
                if (player == null)
                {
                    System.err.println("WARNING: missing player spec for id "
                        + info.playerId);
                    continue;
                }
                boolean wasAdmin = player.admin;
                boolean wasVerified = player.verified;
                boolean isAdmin = info.admin;
                boolean isVerified = info.verified;
                player.admin = info.admin;
                player.registered = info.registered;
                player.verified = info.verified;
                if (isAdmin && !wasAdmin)
                {
                    tryMode("#all", "SERVER", "SERVER", "SERVER", "+o " + player.callsign);
                    if (!isUs)
                        tryJoin("#admin", player.callsign, getPlayerLogin(player),
                                getPlayerHostname(player));
                    tryMode("#admin", "SERVER", "SERVER", "SERVER", "+o " + player.callsign);
                }
                if (!isAdmin && wasAdmin)
                {
                    tryMode("#all", "SERVER", "SERVER", "SERVER", "-o " + player.callsign);
                    if (!isUs)
                        tryPart("#admin", player.callsign, getPlayerLogin(player),
                                getPlayerHostname(player));
                }
                if (isVerified && !wasVerified)
                {
                    tryMode("#all", "SERVER", "SERVER", "SERVER", "+v " + player.callsign);
                }
                if (!isVerified && wasVerified)
                {
                    tryMode("#all", "SERVER", "SERVER", "SERVER", "-v " + player.callsign);
                }
            }
        }
        else if (message instanceof MsgReject)
        {
            MsgReject m = (MsgReject) message;
            System.out.println("BZFlag Rejected: " + m.reason + " " + m.message);
            doShutdown();
            initialConnectQueue.offer(new RuntimeException(
                    "Connection rejected by the server: " + m.reason + " " + m.message));
        }
        else if (message instanceof MsgRemovePlayer)
        {
            MsgRemovePlayer m = (MsgRemovePlayer) message;
            System.out.println("Player #" + m.playerId + " removed.");
            Player player = players[m.playerId];
            if (player == null)
            {
                System.err.println("WARNING: attempted to remove a non-existent player: "
                    + m.playerId);
                return;
            }
            if (player.admin)
                tryPart("#admin", player.callsign, getPlayerLogin(player),
                        getPlayerHostname(player));
            tryPart("#all", player.callsign, getPlayerLogin(player),
                    getPlayerHostname(player));
        }
        else if (message instanceof MsgSetVar)
        {
            MsgSetVar m = (MsgSetVar) message;
            for (Map.Entry<String, String> entry : m.vars.entrySet())
            {
                if (entry.getValue().equals(""))
                    serverVariables.remove(entry.getKey());
                else
                    serverVariables.put(entry.getKey(), entry.getValue());
            }
            // TODO: run some sort of factoid notification when this occurs
        }
        else if (message instanceof MsgSuperKill)
        {
            System.out.println("BZFlag MsgSuperKill received; disconnecting.");
            doShutdown();
            // FIXME: need to call onDisconnect here
        }
        else if (message instanceof MsgTeamUpdate)
        {
            // We're ignoring this for now
        }
    }
    
    private boolean isActionMessage(String message, String callsign)
    {
        return message.startsWith("* " + callsign + " ") && message.endsWith("\t*");
    }
    
    private String getRawAction(String message, String callsign)
    {
        return message.substring(3 + callsign.length(), message.length() - 2);
    }
    
    private String getLocalHostname()
    {
        return getPlayerHostname(getLocalPlayer());
    }
    
    private String getLocalLogin()
    {
        return getPlayerLogin(getLocalPlayer());
    }
    
    private void tryMode(String channel, String source, String sourceLogin,
            String sourceHostname, String mode)
    {
        if (areWeAt(channel))
            context.onMode(channel, source, sourceLogin, sourceHostname, mode);
    }
    
    private void tryJoin(String channel, String sender, String login, String hostname)
    {
        if (areWeAt(channel))
            context.onJoin(channel, sender, login, hostname);
    }
    
    private void tryPart(String channel, String sender, String login, String hostname)
    {
        if (areWeAt(channel))
            context.onPart(channel, sender, login, hostname);
    }
    
    private boolean areWeAt(String channel)
    {
        if (channel.equals("#all"))
            return joinedAll;
        if (channel.equals("#admin"))
            return joinedAdmin;
        if (channel.equals("#team"))
            return joinedTeam;
        return false;
    }
    
    public String getPlayerHostname(Player player)
    {
        String callsign = player.callsign;
        callsign = callsign.replace("_", "__");
        callsign = callsign.replace(" ", "_0");
        callsign = callsign.replace("!", "_1");
        callsign = callsign.replace("@", "_2");
        callsign = callsign.replace("#", "_3");
        callsign = callsign.replace("$", "_4");
        callsign = callsign.replace("%", "_5");
        callsign = callsign.replace("^", "_6");
        callsign = callsign.replace("&", "_7");
        callsign = callsign.replace("*", "_8");
        return (player.verified ? "verified/" : "anonymous/") + callsign;
    }
    
    public String getPlayerLogin(Player player)
    {
        // If anyone has any idea more useful than this...
        // The length of 10, though, was chosen by jcp because it would trim his callsign
        // ("javawizard2539") to exactly "javawizard", which is sorta cool.
        // TODO: if we have the ability to get the player's bzid from the server, then we
        // could use that as the player's login if they're verified.
        String hostname = getPlayerHostname(player);
        hostname = hostname.substring(hostname.indexOf("/") + 1);
        if (hostname.length() > 10)
            hostname = hostname.substring(0, 10);
        return hostname;
    }
    
    public String getPlayerHostname(String callsign)
    {
        return getPlayerHostname(getPlayerByCallsign(callsign));
    }
    
    public String getPlayerLogin(String callsign)
    {
        return getPlayerLogin(getPlayerByCallsign(callsign));
    }
    
    private Player getPlayerByCallsign(String callsign)
    {
        for (Player player : players)
        {
            if (player == null)
                continue;
            if (player.callsign.equalsIgnoreCase(callsign))
                return player;
        }
        return null;
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
        MsgEnter enter = new MsgEnter();
        enter.callsign = context.getNick();
        enter.clientVersion = "javawizard2539's JZBot, http://jzbot.googlecode.com";
        enter.email = "";
        enter.key = "";
        if (context.getPassword() != null && !context.getPassword().equals(""))
            enter.key = requestPasswordToken(context.getNick(), context.getPassword());
        enter.team = OBSERVER;
        enter.type = 0;
        outQueue.offer(enter);
        Object status;
        try
        {
            status = initialConnectQueue.poll(30, TimeUnit.SECONDS);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
            status = e;
        }
        if (status == null)
        {
            doShutdown();
            throw new IOException("The server didn't accept our "
                + "connect request within 30 seconds, so we're disconnecting.");
        }
        else if (status instanceof Throwable)
        {
            doShutdown();
            throw new IOException("An error occurred while connecting to the server",
                    (Throwable) status);
        }
        else if (status == CONNECTION_SUCCESSFUL)
        {
            startOnConnectThread();
            return;
        }
        else
        {
            doShutdown();
            throw new IOException("An unknown error occurred while connecting.");
        }
    }
    
    /**
     * Contacts the BZFlag list server and asks it to get a token for us. If a token can't
     * be retrieved from the server (which, among other reasons, could be because the
     * server is down or the password is incorrect), an exception is thrown. If the token
     * is retrieved successfully, it is returned.
     * 
     * @param nick
     * @param password
     * @return
     */
    private String requestPasswordToken(String nick, String password) throws IOException
    {
        HttpClient client = new DefaultHttpClient();
        client.getParams().setParameter("http.socket.timeout", 20 * 1000);
        HttpPost request = new HttpPost("http://my.bzflag.org/db");
        request.addHeader("Content-type", "application/x-www-form-urlencoded");
        request.setEntity(new StringEntity("action=GETTOKEN&version="
            + URLEncoder.encode("Ask jcp on irc.freenode.net #jzbot")
            + "&local=1&callsign=" + URLEncoder.encode(nick) + "&password="
            + URLEncoder.encode(password)));
        HttpResponse response = client.execute(request);
        int responseCode = response.getStatusLine().getStatusCode();
        if (responseCode != 200)
            throw new RuntimeException("Received response code " + responseCode
                + " from my.bzflag.og/db while trying to authenticate with the "
                + "list server (200 should have been sent instead): "
                + response.getStatusLine().getReasonPhrase() + " with content "
                + Pastebin.readContent(response));
        String content = Pastebin.readContent(response);
        content = content.trim();
        if (content.startsWith("\""))
            content = content.substring(1).trim();
        if (!content.startsWith("TOKEN:"))
            throw new RuntimeException("Content received from the list "
                + "server while attempting to authenticate does "
                + "not start with \"TOKEN:\" (which it should have started with that); "
                + "the content was: " + content);
        content = content.substring("TOKEN:".length());
        return content.trim();
    }
    
    private void startOnConnectThread()
    {
        new Thread("bzflag-onconnect-thread")
        {
            public void run()
            {
                try
                {
                    Thread.sleep(2000);// Wait for some time so that we get the full list
                    // of server players
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                context.onConnect();
            }
        }.start();
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
        // FIXME: This totally ignores the quit message. We should make it so that it
        // doesn't ignore it.
        doShutdown();
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
        return getLocalPlayer().callsign;
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
        // System.out.println("Asked if a bzflag connector is connected");
        if (serverLink == null)
        {
            // System.out.println("No server link, so we aren't connected");
            return false;
        }
        boolean connected = serverLink.isConnected();
        // System.out.println("We have a server link, and we are" + (connected ? "" :
        // " not")
        // + " connected");
        return connected;
    }
    
    @Override
    public void joinChannel(final String channel)
    {
        System.out.println("Requested BZFlag part for " + channel);
        boolean joinWorked = true;
        if (channel.equals("#all"))
            joinedAll = true;
        else if (channel.equals("#admin"))
            joinedAdmin = true;
        else if (channel.equals("#team"))
            joinedTeam = true;
        else
            joinWorked = false;
        if (joinWorked)
            new Thread()
            {
                public void run()
                {
                    context.onJoin(channel, getLocalPlayer().callsign,
                            getPlayerLogin(getLocalPlayer()),
                            getPlayerHostname(getLocalPlayer()));
                }
            }.start();
        // TODO: we should probably report a ban or something via an event, although we'll
        // want to do it on a new thread to prevent deadlocks
    }
    
    @Override
    public void kick(String channel, String user, String reason)
    {
        // Doesn't matter what channel we're kicking from, the format is the same. And
        // kicking in bzflag is just sending a modified message.
        sendMessage("SERVER", "/kick \"" + user + "\" " + reason);
    }
    
    @Override
    public void partChannel(final String channel, String reason)
    {
        System.out.println("Requested BZFlag part for " + channel);
        boolean partWorked = true;
        if (channel.equals("#all"))
            joinedAll = false;
        else if (channel.equals("#admin"))
            joinedAdmin = false;
        else if (channel.equals("#team"))
            joinedTeam = false;
        else
            partWorked = false;
        if (partWorked)
            new Thread()
            {
                public void run()
                {
                    context.onPart(channel, getLocalPlayer().callsign,
                            getPlayerLogin(getLocalPlayer()),
                            getPlayerHostname(getLocalPlayer()));
                }
            }.start();
        
    }
    
    @Override
    public void sendAction(String target, String message)
    {
        // Actions are messages with a specialized format.
        sendMessage(target, "/me " + message);
    }
    
    private Player getLocalPlayer()
    {
        return players[serverLink.getLocalId()];
    }
    
    @Override
    public void sendMessage(String target, String message)
    {
        // TODO: we really should check to make sure that we're connected, but for now, we
        // won't bother.
        debugDumpMessage("BZFlag outbound message: ", message);
        MsgMessage m = new MsgMessage();
        m.message = message;
        if (target.equals("SERVER"))
        {
            m.to = BZFlagConnector.MsgToServerPlayer;
        }
        else if (target.equals("#all"))
        {
            if (!joinedAll)
                return;
            m.to = BZFlagConnector.MsgToAllPlayers;
        }
        else if (target.equals("#admin"))
        {
            if (!joinedAdmin)
                return;
            m.to = BZFlagConnector.MsgToAdmins;
        }
        else if (target.equals("#team"))
        {
            if (!joinedTeam)
                return;
            m.to = BZFlagConnector.MsgToObserverTeam;
        }
        else
        {
            m.to = -1;
            for (Player player : players)
            {
                if (player.callsign.equalsIgnoreCase(target))
                {
                    m.to = player.playerId;
                    break;
                }
            }
            if (m.to == -1)// No such player
                return;
        }
        serverLink.uncheckedSend(m);
    }
    
    private void debugDumpMessage(String string, String message)
    {
        // for (char c : message.toCharArray())
        // {
        // string += ((int) c) + " ";
        // }
        // System.out.println(string);
    }
    
    @Override
    public void sendNotice(String target, String message)
    {
        // BZFlag doesn't have a separate idea of notices from messages, so we'll just
        // send this as a message. In the future, we might want to prefix it with (notice)
        // or something, or make it user-configurable in the bot what notices should be
        // prefixed with on protocols that don't support them.
        sendMessage(target, message);
    }
    
    @Override
    public void setEncoding(String string) throws UnsupportedEncodingException
    {
        // BZFlag always uses ASCII as the encoding, so we'll ignore this.
    }
    
    @Override
    public void setLogin(String nick)
    {
        // BZFlag doesn't allow for a login separate from the nick (meaning it uses the
        // nick as the login), so we'll ignore this.
    }
    
    @Override
    public void setMessageDelay(long ms)
    {
        // Messages delays aren't supported yet.
    }
    
    @Override
    public void setMode(String channel, String mode)
    {
        // Mode changes aren't specific enough for BZFlag's purposes. If the user wants to
        // change other users' groups, they'll have to do it with a protocol-specific
        // function for now.
    }
    
    @Override
    public void setName(String nick)
    {
        // Name changes aren't supported by BZFlag, so we'll just ignore this.
    }
    
    @Override
    public void setTopic(String channel, String topic)
    {
        // Topics aren't supported by BZFlag, so we'll just ignore this.
    }
    
    @Override
    public void setVersion(String string)
    {
        // Not supported right now, although we could potentially use this as our email
        // string in the future. We need to make this user-configurable before we do that,
        // though. Perhaps this could be an additional server property in the data store.
    }
    
    @Override
    public boolean supportsMessageDelay()
    {
        return false;
    }
    
    @Override
    public void discard()
    {
        doShutdown();
    }
    
    @Override
    public void processProtocolFunction(Sink sink, ArgumentList arguments,
            FactContext context)
    {
        throw new UnsupportedOperationException("There aren't yet any "
            + "protocol-specific functions for the BZFlag protocol.");
    }
    
    /**
     * Returns false. Since copy/paste isn't supported from within BZFlag, and neither is
     * automatic URL linking, it's extremely difficult to open a pastebin post from within
     * BZFlag. Coincidentally, BZFlag doesn't have built-in flood protection, so the bot
     * won't flood itself off for sending large amounts of data to the server.
     */
    @Override
    public boolean likesPastebin()
    {
        return false;
    }
    
    @Override
    public void sendInvite(String nick, String channel)
    {
        // TODO: we might want to throw an exception here for now, since BZFlag doesn't
        // really support invites
    }
    
}
