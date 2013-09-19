package jw.jzbot.fact.debug.wire.protocol;

import jw.jzbot.fact.debug.wire.AuthRequest;

public class AuthRequestPacket extends Packet
{
    private AuthRequest request;

    public AuthRequest getRequest()
    {
        return request;
    }

    public void setRequest(AuthRequest request)
    {
        this.request = request;
    }
}
