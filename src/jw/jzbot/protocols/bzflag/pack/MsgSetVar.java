package jw.jzbot.protocols.bzflag.pack;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import jw.jzbot.protocols.bzflag.Message;

public class MsgSetVar extends Message
{
    public Map<String, String> vars = new HashMap<String, String>();
    
    @Override
    public void pack(DataOutputStream out) throws IOException
    {
        out.write(vars.size());
        for (Entry<String, String> entry : vars.entrySet())
        {
            String key = entry.getKey();
            String value = entry.getValue();
            out.writeByte(key.length());
            out.writeBytes(key);
            out.writeByte(value.length());
            out.writeBytes(value);
        }
    }
    
    @Override
    public void unpack(DataInputStream in, int length) throws IOException
    {
        int items = in.readUnsignedShort();
        for (int i = 0; i < items; i++)
        {
            int keyLength = in.readUnsignedByte();
            String key = unpackString(in, keyLength);
            int valueLength = in.readUnsignedByte();
            String value = unpackString(in, valueLength);
            vars.put(key, value);
        }
    }
    
}
