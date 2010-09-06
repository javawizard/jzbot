package jw.jzbot.protocols.xmpp;

import jw.jzbot.protocols.Connection;
import jw.jzbot.protocols.Protocol;

public class NewXmppProtocol implements Protocol
{
    
    @Override
    public Connection createConnection()
    {
        return new XmppConnection();
    }
    
    @Override
    public String getName()
    {
        return "xmpp";
    }
    
    @Override
    public void initialize()
    {
        // TODO Auto-generated method stub
        
    }
    
}
