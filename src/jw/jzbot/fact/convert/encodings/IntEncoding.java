package jw.jzbot.fact.convert.encodings;

import java.math.BigDecimal;
import java.math.BigInteger;

import jw.jzbot.JZBot;
import jw.jzbot.fact.convert.Encoding;
import jw.jzbot.fact.exceptions.FactoidException;

public class IntEncoding implements Encoding
{
    
    @Override
    public Object decode(String data)
    {
        return new BigInteger(data);
    }
    
    @Override
    public String encode(Object data)
    {
        BigInteger i = objectToInt(data);
        return i.toString();
    }
    
    public static BigInteger objectToInt(Object data)
    {
        BigInteger i;
        if (data instanceof BigInteger)
            i = (BigInteger) data;
        else if (data instanceof byte[])
            i = new BigInteger(1, (byte[]) data);
        else
            throw new FactoidException("Unsupported format: " + data.getClass().getName());
        return i;
    }
    
    @Override
    public String getHelp()
    {
        return "An encoding that represents integers in decimal form. Decodes to "
            + "and encodes from integral.";
    }
    
}
