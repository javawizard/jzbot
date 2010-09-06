package jw.jzbot.protocols.fb;

import jw.jzbot.protocols.Connection;
import jw.jzbot.protocols.Protocol;

public class FacebookProtocol implements Protocol
{
    
    @Override
    public Connection createConnection()
    {
        return new FacebookConnection();
    }
    
    @Override
    public String getName()
    {
        return "facebook";
    }

    @Override
    public void initialize()
    {
        // TODO Auto-generated method stub
        
    }
    
}
