package jw.jzbot.fact.debug.wire.protocol;

public class AuthResponsePacket extends Packet
{
    private String[] values;
    private String error;
    
    public String getError()
    {
        return error;
    }
    
    public void setError(String error)
    {
        this.error = error;
    }
    
    public String[] getValues()
    {
        return values;
    }
    
    public void setValues(String[] values)
    {
        this.values = values;
    }
}
