package jw.jzbot.protocols.bzflag;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import jw.jzbot.protocols.bzflag.Packet.Layer;

/**
 * A wrapper around BZFlagConnector that additionally encodes/decodes packets into
 * instances of {@link Message}.
 * 
 * @author Alexander Boyd
 * 
 */
public class ServerLink
{
    private static Map<Integer, Class<? extends Message>> typesToClasses = new HashMap<Integer, Class<? extends Message>>();
    private static Map<Class<? extends Message>, Integer> classesToTypes = new HashMap<Class<? extends Message>, Integer>();
    
    private static void register(int type, Class<? extends Message> c)
    {
        typesToClasses.put(type, c);
        classesToTypes.put(c, type);
    }
    
    static
    {
        // TODO: have this look up the package's contents in the future
        Field[] fields = BZFlagConnector.class.getFields();
        for (Field field : fields)
        {
            if ((field.getModifiers() & Modifier.STATIC) != 0
                    && (field.getModifiers() & Modifier.FINAL) != 0
                    && field.getName().startsWith("Msg"))
            {
                String fieldName = field.getName();
                try
                {
                    @SuppressWarnings("unchecked")
                    Class<? extends Message> c = (Class<? extends Message>) Class
                            .forName("jw.jzbot.protocols.bzflag.pack." + fieldName);
                    register(field.getInt(null), c);
                }
                catch (ClassNotFoundException e)
                {
                }
                catch (IllegalAccessException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
    
    private BZFlagConnector connector;
    
    public ServerLink(String host, int port) throws IOException
    {
        connector = new BZFlagConnector(host, port);
    }
    
    public ServerLink(Socket socket, boolean doInitial) throws IOException
    {
        connector = new BZFlagConnector(socket, doInitial);
    }
    
    public int getLocalId()
    {
        return connector.getSlot();
    }
    
    public void close() throws IOException
    {
        connector.close();
    }
    
    public void closeIgnore()
    {
        try
        {
            connector.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * Same as {@link #send(Message)}, but wraps the send call in a try/catch block that
     * wraps any exception that might be thrown with a RuntimeException and throws it
     * again.
     * 
     * @param message
     */
    public void uncheckedSend(Message message)
    {
        try
        {
            send(message);
        }
        catch (Exception e)
        {
            throw new RuntimeException(
                    "Exception occurred while running uncheckedSend() in ServerLink", e);
        }
    }
    
    public void send(Message message) throws IOException
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(out);
        message.pack(dout);
        byte[] bytes = out.toByteArray();
        Packet packet = new Packet();
        packet.setLayer(Layer.TCP);
        packet.setType(classesToTypes.get(message.getClass()));
        packet.setMessage(bytes);
        connector.send(packet);
    }
    
    public Message receive() throws IOException
    {
        while (true)
        {
            Packet packet = connector.receive(Layer.TCP);
            int type = packet.getType();
            Class<? extends Message> c = typesToClasses.get(type);
            if (c == null)// This is a message type we don't know about
                continue;
            try
            {
                Message message = c.newInstance();
                ByteArrayInputStream in = new ByteArrayInputStream(packet.getMessage());
                DataInputStream din = new DataInputStream(in);
                message.unpack(din, packet.getMessage().length);
                return message;
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }
    }
}
