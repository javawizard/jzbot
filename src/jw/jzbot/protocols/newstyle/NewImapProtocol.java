package jw.jzbot.protocols.newstyle;

import jw.jzbot.protocols.Connection;
import jw.jzbot.protocols.Protocol;
import jw.jzbot.protocols.imap.ImapProtocol;

public class NewImapProtocol implements Protocol
{
    
    @Override
    public Connection createConnection()
    {
        return new ImapProtocol();
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
