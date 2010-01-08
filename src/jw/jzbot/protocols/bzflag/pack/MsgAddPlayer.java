package jw.jzbot.protocols.bzflag.pack;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import jw.jzbot.protocols.bzflag.Message;

public class MsgAddPlayer extends Message
{
    public int id;
    public int type;
    public int team;
    public int wins;
    public int losses;
    public int tks;
    public String callsign;
    public String email;
    
    @Override
    public void pack(DataOutputStream out) throws IOException
    {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void unpack(DataInputStream in, int length) throws IOException
    {
        id = in.readUnsignedByte();
        type = in.readUnsignedShort();
        team = in.readUnsignedShort();
        wins = in.readUnsignedShort();
        losses = in.readUnsignedShort();
        tks = in.readUnsignedShort();
        callsign = unpackString(in, 32);
        email = unpackString(in, 128);
    }
}
