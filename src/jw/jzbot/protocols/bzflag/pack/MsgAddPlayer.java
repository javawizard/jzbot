package jw.jzbot.protocols.bzflag.pack;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import jw.jzbot.protocols.bzflag.Message;

public class MsgAddPlayer extends Message
{
    private int id;
    private int type;
    private int team;
    private int wins;
    private int losses;
    private int tks;
    private String callsign;
    private String email;
    
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
