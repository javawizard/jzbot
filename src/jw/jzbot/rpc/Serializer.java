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
     * @throws IOException
     */
    public static Object deserialize(DataInputStream stream, List<Class> classes)
            throws IOException
    {
        Map<String, Class<?>> map = new HashMap<String, Class<?>>();
        for (Class<?> c : classes)
        {
            String name = c.getSimpleName().toLowerCase();
            if (c.isAnnotationPresent(Type.class))
                name = c.getAnnotation(Type.class).getName();
            map.put(name, c);
        }
        return deserialize(stream, map);
    }
    
    private static Object deserialize(DataInputStream stream, Map<String, Class<?>> classMap)
            throws IOException
    {
        int eType = stream.readByte();
        if (eType == 1)// Associative Array
        {
            int count = stream.readInt();
            Map map = new HashMap();
            for (int i = 0; i < count; i++)
            {
                map.put(deserialize(stream, classMap), deserialize(stream, classMap));
            }
            return map;
        }
        else if (eType == 2)// Object
        {
            String typeName = stream.readUTF();
            int count = stream.readInt();
            Class c = classMap.get(typeName);
            if (c == null)
                throw new IllegalStateException("Object with type \"" + typeName
                    + "\" received, but this type name is not known.");
            try
            {
                Object instance = c.newInstance();
                for (int i = 0; i < count; i++)
                {
                    String key = (String) deserialize(stream, classMap);
                    Object value = deserialize(stream, classMap);
                    Field f = c.getField(key);
                    f.set(instance, value);
                }
                return instance;
            }
            catch (Exception e)
            {
                throw new IllegalStateException(
                        "Exception occurred while reconstructing object", e);
            }
        }
        else if (eType == 3)// List
        {
            int count = stream.readInt();
            ArrayList list = new ArrayList(count);
            for (int i = 0; i < count; i++)
                list.add(deserialize(stream, classMap));
            return list;
        }
        else if (eType == 4)// Integer
        {
            return stream.readInt();
        }
        else if (eType == 5)// Long
        {
            return stream.readLong();
        }
        else if (eType == 6)// Double
        {
            return stream.readDouble();
        }
        else if (eType == 7)// String
        {
            return stream.readUTF();
        }
        else
            throw new IllegalStateException("Unrecognized eType field "
                + "while deserializing: " + eType
                + ", should have been in the range 1-7, inclusive. You "
                + "probably need to get a newer version of the "
                + "serialization library, or have the software that "
                + "created the stuff you're trying to deserialize "
                + "re-create it while using an older version of the "
                + "serialization library.");
    }
}
