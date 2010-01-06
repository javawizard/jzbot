package jw.jzbot.protocols.bzflag;

public class MessageReader
{
    private byte[] data;
    private int index;
    
    public MessageReader(byte[] data, int index)
    {
        this.data = data;
        this.index = index;
    }
    
    
}
