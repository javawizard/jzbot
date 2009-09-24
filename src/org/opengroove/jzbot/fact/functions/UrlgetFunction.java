package org.opengroove.jzbot.fact.functions;

import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.FactoidException;
import org.opengroove.jzbot.fact.Function;

public class UrlgetFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        throw new FactoidException("The urlget function doesn't work yet.");
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Not working yet, but when it is working, this will get the page at the specified "
                + "http url. Only http will be supported (IE no \"file://\" urls), and the bot "
                + "owner will be able to choose whether localhost or 127.0.0.1 works. Both IPv4 "
                + "and IPv6 are supported.";
    }
    
}
