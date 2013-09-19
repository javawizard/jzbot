package jw.jzbot.protocols.imap;

import jw.jzbot.protocols.Connection;
import jw.jzbot.protocols.Protocol;

public class ImapProtocol implements Protocol
{
    
    @Override
    public Connection createConnection()
    {
        return new ImapConnection();
    }
    
    @Override
    public String getName()
    {
        return "imap";
    }

    @Override
    public void initialize()
    {
        // TODO Auto-generated method stub
        
    }
    
}
