package jw.jzbot.protocols.newstyle;

import jw.jzbot.protocols.Connection;
import jw.jzbot.protocols.Protocol;
import jw.jzbot.protocols.xmpp.XmppProtocol;

public class NewXmppProtocol implements Protocol
{
    
    @Override
    public Connection createConnection()
    {
        return new XmppProtocol();
    }
    
    @Override
    public String getName()
    {
        return "xmpp";
    }
    
}
