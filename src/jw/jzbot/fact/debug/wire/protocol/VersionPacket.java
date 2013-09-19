package jw.jzbot.fact.debug.wire.protocol;

public class VersionPacket extends Packet
{
    private int version;
    
    public int getVersion()
    {
        return version;
    }
    
    public void setVersion(int version)
    {
        this.version = version;
    }
}
