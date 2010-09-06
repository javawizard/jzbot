package jw.jzbot.protocols.newstyle;

import jw.jzbot.protocols.Connection;
import jw.jzbot.protocols.Protocol;
import jw.jzbot.protocols.bzflag.BZFlagProtocol;

public class NewBZFlagProtocol implements Protocol
{
    
    @Override
    public Connection createConnection()
    {
        return new BZFlagProtocol();
    }
    
    @Override
    public String getName()
    {
        return "bzflag";
    }

    @Override
    public void initialize()
    {
        // TODO Auto-generated method stub
        
    }
    
}
