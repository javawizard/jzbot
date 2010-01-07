package jw.jzbot.protocols.bzflag.pack;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import jw.jzbot.protocols.bzflag.Message;

/**
 * A client-side message. Note that pack() packs the message without the "from" field, so
 * that it can be sent to the server.
 * 
 * @author Alexander Boyd
 * 
 */
public class MsgMessage extends Message
{
    public int from;
    public int to;
    public String message;
    
    @Override
    public void pack(DataOutputStream out) throws IOException
    {
        out.writeByte(to);
        out.writeBytes(message);
        out.write(0);
    }
    
    @Override
    public void unpack(DataInputStream in, int length) throws IOException
    {
        from = in.readUnsignedByte();
        to = in.readUnsignedByte();
        message = unpackString(in);
    }
}
