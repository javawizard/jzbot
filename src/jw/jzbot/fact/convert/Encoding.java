package jw.jzbot.fact.convert;

public interface Encoding
{
    public String getHelp();
    
    public Object decode(String data);
    
    public String encode(Object data);
}
