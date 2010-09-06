package jw.jzbot.protocols.bzflag;

import jw.jzbot.protocols.Connection;
import jw.jzbot.protocols.Protocol;

public class NewBZFlagProtocol implements Protocol
{
    
    @Override
    public Connection createConnection()
    {
        return new BZFlagProtocolConnection();
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
