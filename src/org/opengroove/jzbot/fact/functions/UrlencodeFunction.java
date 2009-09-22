package org.opengroove.jzbot.fact.functions;

import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.Function;

public class UrlencodeFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{urlencode||<text>}} -- Encodes the text specified so that it is "
                + "suitable for including in a URL.";
    }
    
    @Override
    public String getName()
    {
        return "urlencode";
    }
    
}
