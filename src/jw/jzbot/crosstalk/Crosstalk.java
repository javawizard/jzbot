package jw.jzbot.crosstalk;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.sf.opengroove.common.utils.StringUtils;

import jw.jzbot.ResponseException;
import jw.jzbot.configuration.Configuration;
import jw.jzbot.pastebin.PastebinUtils;
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
    
    private static final Map<String, Session> sessions = new HashMap<String, Session>();
    
    public static void registerHandler(String name, Handler handler)
    {
        handlerRegistry.put(name, handler);
    }
    
    public static void run(String server, String channel, boolean pm, UserMessenger sender,
            Messenger source, String arguments)
    {
        if (pm)
            throw new ResponseException(
                    "Crosstalk can't yet function over private messages.");
        if (arguments.trim().equals(""))
            throw new ResponseException("The __crosstalk__ command needs arguments "
                + "specified. For more help on what crosstalk is, see \"help crosstalk\".");
        int originalLength = arguments.length();
        String[] tokens = StringUtils.split(arguments, " ", 3);
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
        String commandName = tokens[1];
        arguments = tokens[2];
        PendingPacket packet = getPacket(sender, messageId, true);
        packet.text += arguments.trim();
        Command command = new Command(commandName);
        parseInto(packet.text, command);
        Handler handler = handlerRegistry.get(handlerName);
        if (handler == null)
        {
            sendPacket(sender.getNick(), source, messageId, "r", "error", new Response(
                    "type", ErrorType.other.name(), "message",
                    "The specified handler could not be found."));
            return;
        }
        Response response = null;
        try
        {
            response = handler.runCommand(sender, source, command);
        }
        catch (Throwable e)
        {
            sendPacket(sender.getNick(), source, messageId, "r", "error", new Response(
                    "type", ErrorType.other.name(), "message",
                    "An exception occurred during invocation of the handler: "
                        + PastebinUtils.pastebinStack(e) + " -- " + e.getClass().getName()
                        + ": " + e.getMessage()));
            return;
        }
        System.out.println("Sending response");
        sendPacket(sender.getNick(), source, messageId, "r", "ok", response);
    }
    
    private static void processAggregatedCommand(UserMessenger sender, Messenger source,
            String messageId, String arguments)
    {
        PendingPacket packet = getPacket(sender, messageId, false);
        packet.text += arguments.trim();
    }
    
    private static void processResponse(UserMessenger sender, Messenger source,
            String messageId, String arguments)
    {
        String[] tokens = StringUtils.split(arguments, " ", 2);
        String status = tokens[0];
        arguments = tokens[1];
        PendingPacket packet = getPacket(sender, messageId, true);
        packet.text += arguments.trim();
        Response response = new Response();
        parseInto(packet.text, response);
        if (status.equals("ok"))
            dispatchSuccessfulResponse(sender.getNick(), source, messageId, response);
        else
        {
            System.out.println(response.properties);
            ErrorType type;
            try
            {
                type = ErrorType.valueOf(response.properties.get("type"));
            }
            catch (Exception e)
            {
                type = ErrorType.other;
            }
            String message = response.properties.get("message");
            dispatchFailedResponse(sender, source, messageId, type, message);
        }
    }
    
    private static void processAggregatedResponse(UserMessenger sender, Messenger source,
            String messageId, String arguments)
    {
        PendingPacket packet = getPacket(sender, messageId, false);
        packet.text += arguments.trim();
    }
    
    private static void sendPacket(String targetNick, Messenger to, String messageId,
            String type, String info, Packet packet)
    {
        String command = targetNick + ": __crosstalk__ ";
        String normalPrefix = command + type + " " + messageId + " " + info + " ";
        String aggregatePrefix = command + type + "a " + messageId + " ";
        int maxLength = to.getProtocolDelimitedLength();
        int maxAggregateLength = maxLength - aggregatePrefix.length();
        List<String> messages = new ArrayList<String>();
        String encodedText = encodePacket(packet);
        while (!encodedText.equals(""))
        {
            
            if (encodedText.length() <= maxAggregateLength)
            {
                messages.add(encodedText);
                encodedText = "";
            }
            else
            {
                messages.add(encodedText.substring(0, maxAggregateLength));
                encodedText = encodedText.substring(maxAggregateLength);
            }
        }
        if (messages.size() == 0)
            messages.add("");
        for (int i = 0; i < messages.size(); i++)
        {
            String message = messages.get(i);
            if (i < (messages.size() - 1)) // Not the last message
            {
                to.sendMessage(aggregatePrefix + message);
                continue;
            }
            // Last message
            if (message.length() < (maxLength - normalPrefix.length()))
            {
                to.sendMessage(normalPrefix + message);
            }
            else
            {
                to.sendMessage(aggregatePrefix + message);
                to.sendMessage(normalPrefix);
            }
        }
    }
    
    private static String encodePacket(Packet packet)
    {
        List<String> components = new ArrayList<String>();
        for (Map.Entry<String, String> entry : packet.properties.entrySet())
        {
            components.add(URLEncoder.encode(entry.getKey()) + "="
                + URLEncoder.encode(entry.getValue()));
        }
        if (!packet.data.equals(""))
            components.add(URLEncoder.encode(packet.data));
        return StringUtils.delimited(components, "#");
    }
    
    private static void parseInto(String data, Packet packet)
    {
        String[] components = data.split("#");
        System.out.println("Components: " + Arrays.deepToString(components));
        for (String component : components)
        {
            if (component.contains("="))
            {
                String[] tokens = component.split("=", 2);
                String key = URLDecoder.decode(tokens[0]);
                String value = URLDecoder.decode(tokens[1]);
                System.out.println("Key: " + key);
                System.out.println("Value: " + value);
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
    
    /**
     * Starts a crosstalk session with the specified user.
     * 
     * @param channel
     *            The channel, in a form suitable for passing to
     *            {@link ScopeManager#getMessenger(String)}, at which to communicate with
     *            the user
     * @param nick
     *            The nick of the user to start a crosstalk session with
     * @param handler
     *            The name of the handler to be used on the recipient's side
     * @param callback
     *            The callback to use for this session
     */
    public static void start(Messenger channel, String nick, String handler,
            Callback callback)
    {
        Session session = new Session();
        session.handler = handler;
        session.callback = callback;
        String id = createId();
        if (sessions.containsKey(id))
            throw new RuntimeException("Generated id already exists");
        sessions.put(id, session);
        // Re-using dispatchSuccessfulResponse for now
        dispatchSuccessfulResponse(nick, channel, id, null);
    }
    
    private static void dispatchSuccessfulResponse(String senderNick, Messenger source,
            String messageId, Response response)
    {
        Session session = getSession(senderNick, messageId);
        Callback callback = session.callback;
        Command nextCommand;
        try
        {
            nextCommand = callback.nextCommand(response);
        }
        catch (Throwable e)
        {
            sessions.remove(messageId);
            callback.failed(true, ErrorType.other, "Callback threw an exception: "
                + e.getClass().getName() + ": " + e.getMessage());
            return;
        }
        if (nextCommand == null)// This crosstalk session is finished
        {
            sessions.remove(messageId);
            return;
        }
        sendPacket(senderNick, source, messageId, "c", session.handler + " "
            + nextCommand.name, nextCommand);
    }
    
    private static void dispatchFailedResponse(UserMessenger sender, Messenger source,
            String messageId, ErrorType type, String message)
    {
        Session session = getSession(sender.getNick(), messageId);
        Callback callback = session.callback;
        sessions.remove(messageId);
        System.out.println(message);
        callback.failed(false, type, message);
    }
    
    private static Session getSession(String senderNick, String messageId)
    {
        Session session = sessions.get(messageId);
        if (session == null)
            throw new ResponseException("No session for message id " + messageId + " from "
                + senderNick + " currently exists.");
        return session;
    }
    
    static
    {
        DefaultHandlers.init();
    }
}
