package org.opengroove.jzbot.fact.functions;

import java.net.URLDecoder;
import java.net.URLEncoder;

import org.opengroove.jzbot.ConfigVars;
import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.FactoidException;
import org.opengroove.jzbot.fact.Function;

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
