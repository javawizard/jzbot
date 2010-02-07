package jw.jzbot.protocols.xmpp;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.User;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.Roster.SubscriptionMode;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

import jw.jzbot.Connection;
import jw.jzbot.ConnectionContext;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Sink;

public class XmppProtocol implements Connection
{
    private ConnectionContext context;
    
    private XMPPConnection connection;
    
    private Map<String, Chat> chatMap = new HashMap<String, Chat>();
    
    @Override
    public void changeNick(String newnick)
    {
        // silently ignore nick changes
    }
    
    @Override
    public void connect() throws IOException, IrcException
    {
        try
        {
            connection = new XMPPConnection(context.getServer());
            connection.connect();
            installConnectionListener();
            connection.login(context.getNick(), context.getPassword());
            connection.getRoster().setSubscriptionMode(SubscriptionMode.accept_all);
            installMessageListener();
        }
        catch (Exception e)
        {
            discard();
            throw new RuntimeException(e);
        }
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
    
    protected void doReceiveMessage(Chat chat, Message message)
    {
        System.out.println("Incoming XMPP Message from " + message.getFrom() + ": "
            + message.getBody());
        context.onPrivateMessage(escape(message.getFrom()), "user",
                escapeOnlyAccount(message.getFrom()), message.getBody());
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
    public String[] getChannels()
    {
        return new String[0];
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
        return 1200;// For gmail this is actually 2000, but idk about other providers
    }
    
    @Override
    public User[] getUsers(String channel)
    {
        return new User[0];
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
    public boolean likesPastebin()
    {
        return true;
    }
    
    @Override
    public void partChannel(String channel, String reason)
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
    public void sendAction(String target, String message)
    {
        sendMessage(target, message);
    }
    
    @Override
    public void sendMessage(String target, String text)
    {
        try
        {
            getChat(unescape(target)).sendMessage(text);
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
    public void sendInvite(String nick, String channel)
    {
        // TODO Auto-generated method stub
        
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
