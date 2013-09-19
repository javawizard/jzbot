package jw.jzbot.protocols.bzflag.pack;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import jw.jzbot.protocols.bzflag.Message;

public class MsgLagPing extends Message
{
    public int data;
    
    @Override
    public void pack(DataOutputStream out) throws IOException
    {
        out.writeShort(data);
    }
    
    @Override
    public void unpack(DataInputStream in, int length) throws IOException
    {
        data = in.readUnsignedShort();
    }
    
}
