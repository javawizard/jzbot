package org.opengroove.jzbot.fact.functions;

import java.net.URLEncoder;

import org.opengroove.jzbot.ConfigVars;
import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.FactoidException;
import org.opengroove.jzbot.fact.Function;

public class UrlencodeFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        try
        {
            return URLEncoder
                    .encode(arguments.get(0), ConfigVars.charset.get());
        }
        catch (Exception e)
        {
            throw new FactoidException("Exception while encoding URL fragment",
                    e);
        }
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{urlencode||<text>}} -- Encodes the text specified so that it is "
                + "suitable for including in a URL.";
    }
    
    public String getName()
    {
        return "urlencode";
    }
    
}
