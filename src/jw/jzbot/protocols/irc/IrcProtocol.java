package jw.jzbot.protocols.irc;

import jw.jzbot.protocols.Connection;
import jw.jzbot.protocols.Protocol;

public class IrcProtocol implements Protocol
{
    
    @Override
    public Connection createConnection()
    {
        return new IrcConnection();
    }
    
    @Override
    public String getName()
    {
        return "irc";
    }

    @Override
    public void initialize()
    {
        // TODO Auto-generated method stub
        
    }
    
}
