package org.opengroove.jzbot.protocols;

import org.opengroove.jzbot.Protocol;
import org.opengroove.jzbot.JZBot;
import org.jibble.pircbot.PircBot;

public class IrcProtocol extends PircBot implements Protocol
{
    public IrcProtocol()
    {
        super();
    }
    
    @Override
    public int getProtocolDelimitedLength()
    {
        return 400;
    }
    
    @Override
    public void init()
    {
    }
    
}
