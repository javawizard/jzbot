package jw.jzbot.crosstalk;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.sf.opengroove.common.utils.StringUtils;

import jw.jzbot.ResponseException;
import jw.jzbot.configuration.Configuration;
import jw.jzbot.scope.Messenger;
import jw.jzbot.scope.ScopeManager;
import jw.jzbot.scope.UserMessenger;

public class Crosstalk
{
    public static final Random random = new Random();
    
    public static final String VERSION = "1";
    
    private static final Map<String, Handler> handlerRegistry =
            new HashMap<String, Handler>();
    
    private static final Map<String, PendingPacket> pendingPackets =
            new HashMap<String, PendingPacket>();
    
    public static void registerHandler(String name, Handler handler)
    {
        handlerRegistry.put(name, handler);
    }
    
    public static void run(String server, String channel, boolean pm, UserMessenger sender,
            Messenger source, String arguments)
    {
        if (arguments.trim().equals(""))
        {
            source.sendMessage("The __crosstalk__ command needs arguments "
                + "specified. For more help on what crosstalk is, see \"help crosstalk\".");
        }
        int originalLength = arguments.length();
        String[] tokens = StringUtils.split(arguments, " ", 2);
        String type = tokens[0];
        String messageId = tokens[1];
        arguments = tokens[2];
        if (!StringUtils.isMemberOf(type, new String[] { "c", "ca", "r", "ra" }))
            throw new RuntimeException("Invalid crosstalk message type: " + type);
        ensureCrosstalkAllowed(sender, type, messageId);
        if (type.equals("c"))
            processCommand(sender, source, messageId, arguments);
        else if (type.equals("ca"))
            processAggregatedCommand(sender, source, messageId, arguments);
        else if (type.equals("r"))
            processResponse(sender, source, messageId, arguments);
        else if (type.equals("ra"))
            processAggregatedResponse(sender, source, messageId, arguments);
    }
    
    private static void processCommand(UserMessenger sender, Messenger source,
            String messageId, String arguments)
    {
        String[] tokens = StringUtils.split(arguments, " ", 3);
        String handlerName = tokens[0];
        String command = tokens[1];
        arguments = tokens[2];
    }
    
    private static void processAggregatedCommand(UserMessenger sender, Messenger source,
            String messageId, String arguments)
    {
        PendingPacket packet = getPacket(sender, messageId, false);
        parseInto(arguments, packet);
    }
    
    private static void processResponse(UserMessenger sender, Messenger source,
            String messageId, String arguments)
    {
        // TODO Auto-generated method stub
        
    }
    
    private static void processAggregatedResponse(UserMessenger sender, Messenger source,
            String messageId, String arguments)
    {
        PendingPacket packet = getPacket(sender, messageId, false);
        parseInto(arguments, packet);
    }
    
    private static void sendPacket(UserMessenger user, Messenger to, String messageId,
            String type, String info, Packet packet)
    {
        String command = user.getNick() + ": __crosstalk__ ";
        int maxLength = to.getProtocolDelimitedLength();
        Map<String, String> pendingProperties =
                new HashMap<String, String>(packet.properties);
    }
    
    private static void parseInto(String data, Packet packet)
    {
        String[] components = data.split(" ");
        for (String component : components)
        {
            if (component.contains("="))
            {
                String[] tokens = component.split("=", 2);
                String key = URLDecoder.decode(tokens[0]);
                String value = URLDecoder.decode(tokens[1]);
                if (packet.properties.containsKey(key))
                    packet.properties.put(key, packet.properties.get(key) + value);
                else
                    packet.properties.put(key, value);
            }
            else
            {
                packet.data += URLDecoder.decode(component);
            }
        }
    }
    
    private static PendingPacket getPacket(UserMessenger sender, String messageId,
            boolean removeAfter)
    {
        PendingPacket packet;
        if (pendingPackets.containsKey(messageId))
            packet = pendingPackets.get(messageId);
        else
        {
            packet = new PendingPacket();
            packet.messageId = messageId;
            pendingPackets.put(messageId, packet);
        }
        if (removeAfter)
            pendingPackets.remove(messageId);
        return packet;
    }
    
    private static String createId()
    {
        try
        {
            Thread.sleep(10);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        return "" + System.currentTimeMillis() + "" + random.nextInt(1000000);
    }
    
    private static void ensureCrosstalkAllowed(UserMessenger sender, String type,
            String messageId)
    {
        String[] allowed = Configuration.getText("", "crosstalkusers").split(" ");
        String senderPattern = sender.getServerName() + ":" + sender.getHostname();
        if (!StringUtils.isMemberOf(senderPattern, allowed))
        {
            if (type.equals("c"))
                throw new ResponseException(
                        sender.getNick()
                            + ": __crosstalk__ r "
                            + messageId
                            + " error type=other message="
                            + URLEncoder.encode(sender.getNick()
                                + " is not on the "
                                + "allowed crosstalk user list. See ~config global crosstalkusers."));
            else if (type.equals("ca"))
                throw new ResponseException("");
            else if (type.equals("r"))
                throw new RuntimeException("Received response from user not "
                    + "on the allowed list. See ~config global crosstalkusers.");
            else if (type.equals("ra"))
                throw new ResponseException("");
            throw new RuntimeException("THIS SHOULDN'T HAPPEN. Invalid type "
                + "while handling a permissions error.");
        }
    }
}
