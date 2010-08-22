package jw.jzbot.protocols.xmpp;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jibble.pircbot.Colors;
import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.User;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.Roster.SubscriptionMode;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.sasl.SASLDigestMD5Mechanism;
import org.jivesoftware.smack.sasl.SASLPlainMechanism;
import org.jivesoftware.smackx.muc.InvitationListener;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.Occupant;
import org.jivesoftware.smackx.muc.ParticipantStatusListener;
import org.jivesoftware.smackx.muc.UserStatusListener;
import org.xmlpull.mxp1.MXParser;
import org.xmlpull.v1.XmlPullParserException;

import jw.jzbot.ConfigVars;
import jw.jzbot.ConnectionContext;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Sink;
import jw.jzbot.fact.exceptions.FactoidException;
import jw.jzbot.fact.output.DelimitedSink;
import jw.jzbot.protocols.Connection;

public class XmppProtocol implements Connection
{
    static
    {
        installMXP1Hack();
        // SASLAuthentication.registerSASLMechanism("PLAIN", SASLPlainMechanism.class);
        // SASLAuthentication
        // .registerSASLMechanism("DIGEST-MD5", SASLDigestMD5Mechanism.class);
        // SASLAuthentication.supportSASLMechanism("PLAIN", 0);
        // SASLAuthentication.supportSASLMechanism("DIGEST-MD5", 0);
        
        // TODO: Make sure that this isn't going to ever get initialized before we set up
        // the ProxyStorage database, as that could cause problems
        XMPPConnection.DEBUG_ENABLED = ConfigVars.xmppdebug.get().equals("1");
    }
    
    /**
     * Installs a hack into the custom version of MXP1 that JZBot uses. The hack modifies
     * a method of MXP1 that is only supposed to return textual content within an XML
     * element and throw an exception if the content is mixed. This would work if all XMPP
     * clients conformed to the XMPP protocol. Pidgin doesn't, however, and frequently
     * includes mixed content in a message body. The hack essentially removes mixed
     * content tags and preserves everything else, including the text inside the mixed
     * content tags.
     */
    private static void installMXP1Hack()
    {
        MXParser.nextTextHack = new MXParser.NextTextHack()
        {
            
            @Override
            public String nextText(MXParser parser) throws XmlPullParserException,
                    IOException
            {
                if (parser.getEventType() != parser.START_TAG)
                {
                    throw new XmlPullParserException(
                            "parser must be on START_TAG to read next text", parser, null);
                }
                StringBuffer buffer = new StringBuffer();
                boolean lastWasTag = false;
                int nestingLevel = 1;
                while (true)
                {
                    int eventType = parser.next();
                    if (eventType == parser.START_TAG)
                    {
                        lastWasTag = true;
                        nestingLevel += 1;
                        if (buffer.length() == 0
                            || buffer.charAt(buffer.length() - 1) != ' ')
                            buffer.append(" ");
                    }
                    else if (eventType == parser.END_TAG)
                    {
                        lastWasTag = true;
                        nestingLevel -= 1;
                        if (buffer.length() == 0
                            || buffer.charAt(buffer.length() - 1) != ' ')
                            buffer.append(" ");
                        if (nestingLevel == 0)
                        {
                            String result = buffer.toString();
                            if (lastWasTag && result.endsWith(" "))
                                result = result.substring(0, result.length() - 1);
                            return result;
                        }
                    }
                    else if (eventType == parser.TEXT)
                    {
                        lastWasTag = false;
                        buffer.append(parser.getText());
                    }
                    else
                        throw new XmlPullParserException("Invalid event type "
                            + parser.TYPES[eventType]);
                }
            }
        };
    }
    
    public class XmppUser implements User
    {
        private String nick;
        
        // FIXME: admin/op/w/e stuff needs to be implemented
        /**
         * @param nick
         *            The user's full escaped name. This is *not* their nickname as
         *            visible to XMPP, unless the room is anonymous. If the room is not
         *            anonymous, then this is the user's escaped name complete with
         *            resource.
         */
        public XmppUser(String nick)
        {
            this.nick = nick;
        }
        
        @Override
        public String getNick()
        {
            return nick;
        }
        
        @Override
        public boolean hasVoice()
        {
            return false;
        }
        
        @Override
        public boolean isAdmin()
        {
            return false;
        }
        
        @Override
        public boolean isFounder()
        {
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
            return false;
        }
        
    }
    
    private ConnectionContext context;
    
    private XMPPConnection connection;
    
    private Map<String, Chat> chatMap = new HashMap<String, Chat>();
    /**
     * A mapping of XMPP chat names to XMPP chat rooms. The names are in XMPP format,
     * *not* escaped format. When a chat room is joined, a single MultiUserChat object is
     * added here. This object is *not* removed when the chat room is parted; instead, the
     * object is re-used if the user tries to join again.
     */
    private Map<String, MultiUserChat> multiChatMap = new HashMap<String, MultiUserChat>();
    
    @Override
    public void changeNick(String newnick)
    {
        // silently ignore nick changes for now
    }
    
    @Override
    public void connect() throws IOException, IrcException
    {
        try
        {
            String serverName = context.getServer();
            if (serverName.contains(":"))
            {
                String[] tokens = serverName.split("\\:");
                serverName = tokens[0];
                String hostname = tokens[1];
                int port = -1;
                if (tokens.length > 2)
                    port = Integer.parseInt(tokens[2]);
                ConnectionConfiguration config =
                        new ConnectionConfiguration(hostname, port, serverName);
                connection = new XMPPConnection(config);
            }
            else
            {
                connection = new XMPPConnection(serverName);
            }
            connection.connect();
            installConnectionListener();
            connection.login(context.getNick(), context.getPassword());
            connection.getRoster().setSubscriptionMode(SubscriptionMode.accept_all);
            installMessageListener();
            installInvitationListener();
            new Thread()
            {
                public void run()
                {
                    context.onConnect();
                }
            }.start();
        }
        catch (Exception e)
        {
            discard();
            throw new RuntimeException(e);
        }
    }
    
    private void installInvitationListener()
    {
        MultiUserChat.addInvitationListener(connection, new InvitationListener()
        {
            
            @Override
            public void invitationReceived(XMPPConnection connection, String room,
                    String inviter, String reason, String password, Message message)
            {
                System.out.println("XMPP room invite from " + inviter + " for " + room
                    + " password " + password + " with reason \"" + reason
                    + "\" and message \"" + message + "\".");
                context.onInvitation(escape(connection.getUser()), escape(inviter), "user",
                        escapeOnlyAccount(inviter), "#" + escape(room));
            }
        });
    }
    
    private void installMessageListener()
    {
        connection.addPacketListener(new PacketListener()
        {
            
            @Override
            public void processPacket(Packet packet)
            {
                Message message = (Message) packet;
                doReceiveMessage(null, message);
            }
        }, new PacketTypeFilter(Message.class));
    }
    
    private void installConnectionListener()
    {
        connection.addConnectionListener(new ConnectionListener()
        {
            
            @Override
            public void connectionClosed()
            {
                context.onDisconnect();
            }
            
            @Override
            public void connectionClosedOnError(Exception arg0)
            {
                try
                {
                    connection.disconnect();
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
                context.onDisconnect();
            }
            
            @Override
            public void reconnectingIn(int arg0)
            {
            }
            
            @Override
            public void reconnectionFailed(Exception arg0)
            {
            }
            
            @Override
            public void reconnectionSuccessful()
            {
            }
        });
    }
    
    private synchronized Chat getChat(String jid)
    {
        Chat chat = chatMap.get(jid);
        if (chat == null)
            chat = connection.getChatManager().createChat(jid, new MessageListener()
            {
                
                @Override
                public void processMessage(Chat chat, Message message)
                {
                }
            });
        return chat;
    }
    
    protected void doReceiveMessage(Chat chat, final Message message)
    {
        if (message.getType() != Message.Type.chat)
        {
            System.out.println("Direct message with a non-chat type "
                + "was received and will be ignored.");
            return;
        }
        if (message.getBody() == null)
        {
            System.out.println("Null XMPP message from " + message.getFrom()
                + ". This message will be ignored.");
            return;
        }
        System.out.println("Incoming XMPP Direct Message");
        System.out.println("    From:         " + message.getFrom());
        System.out.println("    To:           " + message.getTo());
        System.out.println("    Type:         " + message.getType());
        System.out.println("    Subject:      " + message.getSubject());
        System.out.println("    Body:         " + message.getBody());
        context.onPrivateMessage(escape(message.getFrom()), "user",
                escapeOnlyAccount(message.getFrom()), message.getBody());
        new Thread()
        {
            public void run()
            {
                System.out.println(message.getBody());
            }
        }.start();
    }
    
    private String escapeOnlyAccount(String from)
    {
        if (from.indexOf("/") != -1)
            from = from.substring(0, from.indexOf("/"));
        return escape(from);
    }
    
    @Override
    public void discard()
    {
        try
        {
            connection.disconnect();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        try
        {
            // Maybe not do this as it could result in double connection disconnect
            // notifications
            context.onDisconnect();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
    
    @Override
    public void disconnect(String message)
    {
        discard();
    }
    
    @Override
    public synchronized String[] getChannels()
    {
        ArrayList<String> list = new ArrayList<String>();
        for (Map.Entry<String, MultiUserChat> entry : new ArrayList<Map.Entry<String, MultiUserChat>>(
                multiChatMap.entrySet()))
        {
            if (entry.getValue().isJoined())
                list.add("#" + escape(entry.getKey()));
        }
        return list.toArray(new String[0]);
    }
    
    @Override
    public String getNick()
    {
        return escape(connection.getUser());
    }
    
    @Override
    public int getOutgoingQueueSize()
    {
        return 0;
    }
    
    @Override
    public int getProtocolDelimitedLength()
    {
        return 800;// GMail uses 2000 as the longest allowed message length; Facebook uses
        // 1023. This seems like a good value to guarantee that everything, including
        // other providers, works as expected.
    }
    
    @Override
    public synchronized User[] getUsers(String channel)
    {
        MultiUserChat room = multiChatMap.get(unescapeChannel(channel));
        if (!room.isJoined())
            room = null;
        if (room == null)
            return new User[0];
        Iterator<String> occupants = room.getOccupants();
        ArrayList<User> results = new ArrayList<User>();
        while (occupants.hasNext())
        {
            String occupantName = occupants.next();
            Occupant o = room.getOccupant(occupantName);
            String nameToUse = o.getJid();// Try the user's full jid first
            if (nameToUse == null || nameToUse.equals(""))// If we don't know their full
                // jid (such as in anonymous rooms), fall back to their MUC name
                nameToUse = occupantName;
            results.add(new XmppUser(escape(nameToUse)));
        }
        return results.toArray(new User[0]);
    }
    
    private String unescapeChannel(String channel)
    {
        if (channel.length() == 0 || !channel.startsWith("#"))
            return "";
        return unescape(channel.substring(1));
    }
    
    @Override
    public void init(ConnectionContext context)
    {
        this.context = context;
    }
    
    @Override
    public boolean isConnected()
    {
        if (connection == null)
            return false;
        return connection.isConnected();
    }
    
    @Override
    public synchronized void joinChannel(String channel)
    {
        System.out.println("XMPP join for " + channel);
        String unescapedChannel = unescapeChannel(channel);
        boolean alreadySetUp = false;
        MultiUserChat room = null;
        if (multiChatMap.get(unescapedChannel) != null)
        {
            alreadySetUp = true;
            room = multiChatMap.get(unescapedChannel);
        }
        if (!alreadySetUp)
        {
            room = new MultiUserChat(connection, unescapeChannel(channel));
            installRoomListeners(room);
        }
        String requestedNickname = escapeOnlyAccount(connection.getUser());
        try
        {
            room.join(requestedNickname);
        }
        catch (XMPPException e)
        {
            e.printStackTrace();
            if (e.getXMPPError().getCode() == 409)// 409: Nickname already in use, so
            // we'll try with a different nickname
            {
                int index = 1;
                for (int i = 0; i < 25; i++)// 25 attempts to join the room
                {
                    try
                    {
                        room.join(requestedNickname + index);
                        break;
                    }
                    catch (Exception e2)
                    {
                        e2.printStackTrace();
                    }
                    if (i == 24)
                        throw new RuntimeException(
                                "Tried several nicknames but all are in use. "
                                    + "Initial exception is attached.", e);
                }
            }
            else
            {
                throw new RuntimeException("Exception while joining " + channel
                    + " (a.k.a " + unescapedChannel + ")", e);
            }
        }
        if (!alreadySetUp)
            multiChatMap.put(room.getRoom(), room);
    }
    
    private void installRoomListeners(final MultiUserChat room)
    {
        final String escapedRoomName = "#" + escape(room.getRoom());
        room.addMessageListener(new PacketListener()
        {
            
            @Override
            public void processPacket(Packet packet)
            {
                Message message = (Message) packet;
                if (message.getFrom().endsWith("/" + room.getNickname()))
                {
                    System.out.println("Ignoring our own room "
                        + "message being sent back to us");
                    return;
                }
                String occupantJid = getJidOrNick(room, message.getFrom());
                System.out.println("XMPP Room Message:");
                System.out.println("    Room:         " + escapedRoomName);
                System.out.println("    From (XMPP):  " + message.getFrom());
                System.out.println("    To   (XMPP):  " + message.getTo());
                System.out.println("    From JID:     " + occupantJid);
                System.out.println("    Type:         " + message.getType());
                System.out.println("    Body:         " + message.getBody());
                if (message.getBody().equals(""))
                {
                    System.out
                            .println("    *** Empty message body. This message will be ignored.");
                    return;
                }
                context.onMessage(escapedRoomName, escape(occupantJid), "user",
                        escapeOnlyAccount(occupantJid), message.getBody());
            }
        });
        room.addParticipantStatusListener(new ParticipantStatusListener()
        {
            private Map<String, String> roomNamesToJids = new HashMap<String, String>();
            
            @Override
            public void adminGranted(String arg0)
            {
            }
            
            @Override
            public void adminRevoked(String arg0)
            {
            }
            
            @Override
            public void banned(String user, String banner, String reason)
            {
                kicked(user, banner, "Banned: " + reason);
            }
            
            @Override
            public void joined(String user)
            {
                String oldUser = user;
                user = getJidOrNick(room, user);
                roomNamesToJids.put(oldUser, user);
                context.onJoin(escapedRoomName, escape(user), "user",
                        escapeOnlyAccount(user));
            }
            
            @Override
            public void kicked(String user, String kicker, String reason)
            {
                String jid = roomNamesToJids.remove(user);
                if (jid == null)
                    jid = user;
                kicker = getJidOrNick(room, kicker);
                context.onKick(escapedRoomName, escape(kicker), "user",
                        escapeOnlyAccount(kicker), escape(jid), reason);
            }
            
            @Override
            public void left(String user)
            {
                String jid = roomNamesToJids.remove(user);
                if (jid == null)
                    jid = user;
                System.out.println("XMPP Left room");
                System.out.println("    Room:      " + room.getRoom());
                System.out.println("    JID:       " + jid);
                System.out.println("    User:      " + user);
                context
                        .onPart(escapedRoomName, escape(jid), "user",
                                escapeOnlyAccount(jid));
            }
            
            @Override
            public void membershipGranted(String arg0)
            {
            }
            
            @Override
            public void membershipRevoked(String arg0)
            {
            }
            
            @Override
            public void moderatorGranted(String arg0)
            {
            }
            
            @Override
            public void moderatorRevoked(String arg0)
            {
            }
            
            @Override
            public void nicknameChanged(String from, String to)
            {
            }
            
            @Override
            public void ownershipGranted(String arg0)
            {
            }
            
            @Override
            public void ownershipRevoked(String arg0)
            {
            }
            
            @Override
            public void voiceGranted(String arg0)
            {
            }
            
            @Override
            public void voiceRevoked(String arg0)
            {
            }
        });
    }
    
    private static String getJidOrNick(MultiUserChat room, String name)
    {
        Occupant o = room.getOccupant(name);
        if (o == null)
            return name;
        if (o.getJid() == null)
            return name;
        if (o.getJid().equals(""))
            return name;
        return o.getJid();
    }
    
    @Override
    public void kick(String channel, String user, String reason)
    {
        // Ignore kicks for now. We need to add this later.
    }
    
    @Override
    public boolean likesPastebin()
    {
        return true;
    }
    
    @Override
    public synchronized void partChannel(String channel, String reason)
    {
        MultiUserChat room = multiChatMap.get(unescapeChannel(channel));
        if (room == null)
            return;
        if (room.isJoined())
            room.leave();
    }
    
    @Override
    public void processProtocolFunction(Sink sink, ArgumentList arguments,
            FactContext context)
    {
        if (arguments.length() == 0)
            throw new FactoidException("Need to specify a command to run. See "
                + "http://code.google.com/p/jwutils/source/browse/trunk/projects"
                + "/jzbot2-old/src/jw/jzbot/protocols/xmpp/XmppProtocol.java and "
                + "search for \"processProtocolFunction\" for a list of commands.");
        if (arguments.getString(0).equals("account-to-nick"))
        {
            sink.write(escape(arguments.resolveString(1)));
            return;
        }
        else if (arguments.getString(0).equals("nick-to-account"))
        {
            sink.write(unescape(arguments.resolveString(1)));
            return;
        }
        else if (arguments.getString(0).equals("status"))
        {
            String modeString = arguments.resolveString(1);
            String status = "";
            if (arguments.length() > 2)
                status = arguments.resolveString(2);
            int priority = 1;
            if (arguments.length() > 3)
                priority = Integer.parseInt(arguments.resolveString(3));
            Presence.Mode mode = Presence.Mode.valueOf(modeString);
            Presence presence =
                    new Presence(Presence.Type.available, status, priority, mode);
            connection.sendPacket(presence);
        }
        else if (arguments.getString(0).equals("statusmodes"))
        {
            DelimitedSink delimited = new DelimitedSink(sink, " ");
            for (Presence.Mode mode : Presence.Mode.values())
            {
                delimited.next().write(mode.name());
            }
        }
        else if (arguments.getString(0).equals("userstatus"))
        {
            String account = arguments.resolveString(1);
            Presence presence;
            if (account.contains("/"))
                presence = connection.getRoster().getPresenceResource(account);
            else
                presence = connection.getRoster().getPresence(account);
            if (presence == null)
            {
                System.out.println("No presence information for " + account);
                return;
            }
            if (presence.getStatus() == null)
            {
                System.out
                        .println("Presence does not have a status message for " + account);
                return;
            }
            sink.write(presence.getStatus());
        }
        else if (arguments.getString(0).equals("userpresence"))
        {
            String account = arguments.resolveString(1);
            Presence presence;
            if (account.contains("/"))
                presence = connection.getRoster().getPresenceResource(account);
            else
                presence = connection.getRoster().getPresence(account);
            if (presence == null)
            {
                System.out.println("No presence information for " + account);
                return;
            }
            System.out.println("Presence type from {p|userpresence} is "
                + presence.getType().name());
            if (presence.getMode() == null && presence.getType() == Presence.Type.available)
            {
                sink.write(Presence.Mode.available.name());
                return;
            }
            if (presence.getMode() == null)
            {
                System.out.println("Presence does not have a mode for " + account);
                return;
            }
            sink.write(presence.getMode().name());
        }
        else if (arguments.getString(0).equals("roster"))
        {
            DelimitedSink delimited = new DelimitedSink(sink, " ");
            for (RosterEntry entry : connection.getRoster().getEntries())
            {
                delimited.next().write(entry.getUser());
            }
        }
        else if (arguments.getString(0).equals("rostername"))
        {
            RosterEntry entry = connection.getRoster().getEntry(arguments.resolveString(1));
            if (entry == null)
                return;
            if (entry.getName() == null)
                return;
            sink.write(entry.getName());
        }
        else if (arguments.getString(0).equals("rostertype"))
        {
            RosterEntry entry = connection.getRoster().getEntry(arguments.resolveString(1));
            if (entry == null)
                return;
            if (entry.getType() == null)
                return;
            sink.write(entry.getType().name());
        }
        else if (arguments.getString(0).equals("presencerequest"))
        {
            String to = arguments.resolveString(1);
            String typeString = arguments.resolveString(2);
            Presence.Type type = Presence.Type.valueOf(typeString);
            if (type == Presence.Type.available)
                throw new FactoidException("Available presence through "
                    + "{p|presencerequest} is not supported right now. Use "
                    + "{p|status} instead.");
            Presence packet = new Presence(type);
            packet.setTo(to);
            connection.sendPacket(packet);
        }
        else
        {
            throw new FactoidException("Invalid command \"" + arguments.getString(0)
                + "\". Try running {p}; the error message issued "
                + "will contain the list of allowed commands.");
        }
    }
    
    @Override
    public void sendAction(String target, String message)
    {
        sendMessage(target, "<- " + message);
    }
    
    @Override
    public synchronized void sendMessage(String target, String text)
    {
        text = Colors.removeFormattingAndColors(text).replaceAll("[\\x00-\\x1f]", "");
        try
        {
            if (target.startsWith("#"))
            {
                MultiUserChat room = multiChatMap.get(unescapeChannel(target));
                if (room != null && room.isJoined())
                    room.sendMessage(text);
            }
            else
            {
                Chat chat = getChat(unescape(target));
                if (chat != null)
                    chat.sendMessage(text);
            }
        }
        catch (XMPPException e)
        {
            e.printStackTrace();
        }
    }
    
    @Override
    public void sendNotice(String target, String message)
    {
        sendMessage(target, "(notice) " + message);
    }
    
    @Override
    public void setEncoding(String string) throws UnsupportedEncodingException
    {
    }
    
    @Override
    public void setLogin(String nick)
    {
    }
    
    @Override
    public void setMessageDelay(long ms)
    {
    }
    
    @Override
    public void setMode(String channel, String mode)
    {
    }
    
    @Override
    public void setName(String nick)
    {
    }
    
    @Override
    public void setTopic(String channel, String topic)
    {
        // FIXME: add support for this
    }
    
    @Override
    public void setVersion(String string)
    {
    }
    
    @Override
    public boolean supportsMessageDelay()
    {
        return false;
    }
    
    @Override
    public synchronized void sendInvite(String nick, String channel)
    {
        MultiUserChat room = multiChatMap.get(unescapeChannel(channel));
        if (room == null || !room.isJoined())// Can't invite a user to a chat room that
            // we're not already joined to
            return;
        room.invite(unescape(nick), "Invitation from JZBot to join a chat room");
    }
    
    private static final String escapeTable =
            "_u-.dot-@at- space-#hash-|pipe-!bang-*star-%percent-"
                + "\"dquote-'quote-/slash-\\backslash";
    
    public static String escape(String jid)
    {
        for (String s : escapeTable.split("-"))
        {
            jid = jid.replace(s.substring(0, 1), "_" + s.substring(1) + "_");
        }
        return jid;
    }
    
    public static String unescape(String escaped)
    {
        ArrayList<String> list =
                new ArrayList<String>(Arrays.asList(escapeTable.split("-")));
        Collections.reverse(list);
        for (String s : list)
        {
            escaped = escaped.replace("_" + s.substring(1) + "_", s.substring(0, 1));
        }
        return escaped;
    }
}
