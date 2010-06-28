package jw.jzbot.fact.convert.encodings;

import java.math.BigInteger;

import jw.jzbot.fact.convert.Encoding;

public class HexEncoding implements Encoding
{
    
    @Override
    public Object decode(String data)
    {
        return new BigInteger(data, 16);
    }
    
    @Override
    public String encode(Object data)
    {
        BigInteger i = IntEncoding.objectToInt(data);
        return i.toString(16);
    }
    
    @Override
    public String getHelp()
    {
        return "An encoding representing a string of hex digits. Decoding removes "
            + "all non-alphanumeric characters, so they can be present without "
            + "problems occurring. Encoding does not separate output with spaces.";
    }
    
}
