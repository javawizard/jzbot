package jw.jzbot.protocols.bzflag;

public class Packet
{
    public static enum Layer
    {
        TCP, UDP
    }
    
    private Layer layer;
    private int type;
    private byte[] message;
    
    public Layer getLayer()
    {
        return layer;
    }
    
    public void setLayer(Layer layer)
    {
        this.layer = layer;
    }
    
    public Packet()
    {
        super();
    }
    
    public Packet(Layer layer, int type, byte[] message)
    {
        super();
        this.layer = layer;
        this.type = type;
        this.message = message;
    }
    
    public int getType()
    {
        return type;
    }
    
    public void setType(int type)
    {
        this.type = type;
    }
    
    public byte[] getMessage()
    {
        return message;
    }
    
    public void setMessage(byte[] message)
    {
        this.message = message;
    }
}
