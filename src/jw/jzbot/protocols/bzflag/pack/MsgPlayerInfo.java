package jw.jzbot.protocols.bzflag.pack;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import jw.jzbot.protocols.bzflag.Message;

public class MsgPlayerInfo extends Message
{
    public Info[] info;
    
    public static class Info
    {
        public int playerId;
        public boolean registered;
        public boolean verified;
        public boolean admin;
    }
    
    @Override
    public void pack(DataOutputStream out) throws IOException
    {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void unpack(DataInputStream in, int length) throws IOException
    {
        int items = in.readUnsignedByte();
        info = new Info[items];
        for (int i = 0; i < items; i++)
        {
            Info t = new Info();
            t.playerId = in.readUnsignedByte();
            int spec = in.readUnsignedByte();
            t.registered = (spec & 1) != 0;
            t.verified = (spec & 2) != 0;
            t.admin = (spec & 4) != 0;
            info[i] = t;
        }
    }
}
