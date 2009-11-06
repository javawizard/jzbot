package jw.jzbot.fact.functions.net;

import java.net.URLDecoder;
import java.net.URLEncoder;

import jw.jzbot.ConfigVars;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.FactoidException;
import jw.jzbot.fact.Function;


public class UrldecodeFunction extends Function
{
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        try
        {
            return URLDecoder
                    .decode(arguments.get(0), ConfigVars.charset.get());
        }
        catch (Exception e)
        {
            throw new FactoidException("Exception while decoding URL fragment",
                    e);
        }
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{urldecode||<text>}} -- Decodes the url-encoded text specified.";
    }
    
}
