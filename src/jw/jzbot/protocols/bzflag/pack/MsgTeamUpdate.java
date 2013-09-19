package jw.jzbot.protocols.bzflag.pack;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import jw.jzbot.protocols.bzflag.Message;

public class MsgTeamUpdate extends Message
{
    public TeamSpec[] teams;
    
    public static class TeamSpec
    {
        public int id;
        public int playerCount;
        public int wins;
        public int losses;
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
        teams = new TeamSpec[items];
        for (int i = 0; i < items; i++)
        {
            TeamSpec team = new TeamSpec();
            team.id = in.readUnsignedShort();
            team.playerCount = in.readUnsignedShort();
            team.wins = in.readUnsignedShort();
            team.losses = in.readUnsignedShort();
            teams[i] = team;
        }
    }
    
}
