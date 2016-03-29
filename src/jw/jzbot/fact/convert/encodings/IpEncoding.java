package jw.jzbot.fact.convert.encodings;

import java.math.BigInteger;

import jw.jzbot.fact.convert.Encoding;
import jw.jzbot.help.Help;
import jw.jzbot.help.HelpPage;

public class IpEncoding implements Encoding
{
    
    @Override
    public Object decode(String data)
    {
        String[] components = data.split("\\.");
        long result = 0;
        result += Integer.parseInt(components[0]) * (1 << 24);
        result += Integer.parseInt(components[1]) * (1 << 16);
        result += Integer.parseInt(components[2]) * (1 << 8);
        result += Integer.parseInt(components[3]);
        return new BigInteger("" + result);
    }
    
    @Override
    public String encode(Object data)
    {
        BigInteger i = (BigInteger) data;
        long value = i.longValue();
        int first = (int) ((value >> 24) & 0xFF);
        int second = (int) ((value >> 16) & 0xFF);
        int third = (int) ((value >> 8) & 0xFF);
        int fourth = (int) (value & 0xFF);
        return first + "." + second + "." + third + "." + fourth;
    }
    
    @Override
    public HelpPage getHelp()
    {
        return Help.build("An encoding representing ip addresses in standard dotted decimal "
                + "notation. Decodes to integral and byte array, and encodes from "
                + "integral and byte array, provided the byte array has exactly "
                + "four bytes. Encoding precedence order is integral, byte array.");
    }
    
}
