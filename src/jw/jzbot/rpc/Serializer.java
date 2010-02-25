package jw.jzbot.rpc;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A class that can serialize and deserialize objects using the RPC Serialization
 * mechanism.
 * 
 * @author Alexander Boyd
 * 
 */
public class Serializer
{
    /**
     * Writes the specified object to the specified stream.
     * 
     * @param object
     * @param stream
     * @throws IOException
     */
    public static void serialize(Object object, DataOutputStream stream) throws IOException
    {
        if (object instanceof Map)
            serializeAssociativeArray(stream, (Map<?, ?>) object);
        else if (object instanceof List || object.getClass().isArray())
            serializeList(stream, object);
        else if (object instanceof Integer)
            serializeInteger(stream, (Integer) object);
        else if (object instanceof Long)
            serializeLong(stream, (Long) object);
        else if (object instanceof Double)
            serializeDouble(stream, (Double) object);
        else if (object instanceof String)
            serializeString(stream, (String) object);
        else
            serializeObject(stream, object);
    }
    
    private static void serializeAssociativeArray(DataOutputStream stream, Map<?, ?> object)
            throws IOException
    {
        stream.write(1);
        stream.writeInt(object.size());
        for (Map.Entry entry : object.entrySet())
        {
            serialize(entry.getKey(), stream);
            serialize(entry.getValue(), stream);
        }
    }
    
    private static void serializeObject(DataOutputStream stream, Object object)
            throws IOException
    {
        if (!object.getClass().isAnnotationPresent(Serialized.class))
            throw new NotSerializableException("Class " + object.getClass().getName()
                + " does not possess the @Serialized annotation. This "
                + "annotation is required for this object to be serialized.");
        stream.write(2);
        String typeName = object.getClass().getSimpleName().toLowerCase();
        if (object.getClass().isAnnotationPresent(Type.class))
        {
            typeName = object.getClass().getAnnotation(Type.class).getName();
        }
        stream.writeUTF(typeName);
        ArrayList<Field> fieldsToUse = new ArrayList<Field>();
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields)
        {
            if (field.isAnnotationPresent(Serialized.class))
                fieldsToUse.add(field);
        }
        stream.writeInt(fieldsToUse.size());
        for (Field field : fieldsToUse)
        {
            Object value;
            try
            {
                value = field.get(object);
            }
            catch (IllegalAccessException e)
            {
                throw new RuntimeException(e);
            }
            String key = field.getName();
            serialize(key, stream);
            serialize(value, stream);
        }
    }
    
    private static void serializeList(DataOutputStream stream, Object list)
            throws IOException
    {
        if (list.getClass().isArray())
            throw new IllegalArgumentException("Class " + list.getClass().getName()
                + " is an array type. Arrays are "
                + "not currently supported. For now, use instances of "
                + "java.util.List. Arrays.asList can wrap an array "
                + "and turn it into a list; you could consider using that instead.");
        List<?> object = (List<?>) list;
        stream.write(3);
        stream.writeInt(object.size());
        for (Object o : object)
        {
            serialize(o, stream);
        }
    }
    
    private static void serializeInteger(DataOutputStream stream, int integer)
            throws IOException
    {
        stream.write(4);
        stream.writeInt(integer);
    }
    
    private static void serializeLong(DataOutputStream stream, long l) throws IOException
    {
        stream.write(5);
        stream.writeLong(l);
    }
    
    private static void serializeDouble(DataOutputStream stream, double d)
            throws IOException
    {
        stream.write(6);
        stream.writeDouble(d);
    }
    
    private static void serializeString(DataOutputStream stream, String s)
            throws IOException
    {
        stream.write(7);
        stream.writeUTF(s);
    }
    
    /**
     * Reads a single object from the specified stream and returns it.
     * 
     * @param stream
     * 
     * @return
     * @throws IOException
     */
    // public static Object deserialize(DataInputStream stream) throws IOException
    // {
    // int eType = stream.readByte();
    // if (eType == 1)// Associative Array
    // {
    // int count = stream.readInt();
    // Map<?, ?> map = new HashMap<?, ?>();
    // for (int i = 0; i < count; i++)
    // {
    // map.put(deserialize(stream), deserialize(stream));
    // }
    // }
    // }
}
