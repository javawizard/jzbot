package jw.jzbot.protocols.bzflag.pack;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import jw.jzbot.protocols.bzflag.Message;

public class MsgEnter extends Message
{
    public int type;
    public int team;
    public String callsign;
    public String email;
    public String key;
    public String clientVersion;
    
    @Override
    public void pack(DataOutputStream out) throws IOException
    {
        out.writeShort(type);
        out.writeShort(team);
        packStringPad()
    }
    
    @Override
    public void unpack(DataInputStream in, int length) throws IOException
    {
        throw new UnsupportedOperationException(
                "There isn't a need to receive a MsgEnter from the server as of now.");
    }
    
}
