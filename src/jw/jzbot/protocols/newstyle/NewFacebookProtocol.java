package jw.jzbot.protocols.newstyle;

import jw.jzbot.protocols.Connection;
import jw.jzbot.protocols.Protocol;
import jw.jzbot.protocols.fb.FacebookProtocol;

public class NewFacebookProtocol implements Protocol
{
    
    @Override
    public Connection createConnection()
    {
        return new FacebookProtocol();
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
