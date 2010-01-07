package jw.jzbot.protocols.bzflag.pack;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import jw.jzbot.protocols.bzflag.Message;

public class MsgReject extends Message
{
    public int reason;
    public String message;
    
    @Override
    public void pack(DataOutputStream out) throws IOException
    {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void unpack(DataInputStream in, int length) throws IOException
    {
        reason = in.readUnsignedShort();
        message = unpackString(in);
    }
    
}
