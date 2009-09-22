package org.opengroove.jzbot.fact.functions;

import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.FactoidException;
import org.opengroove.jzbot.fact.Function;

public class ErrorFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        throw new FactoidException("Factoid error: " + arguments.get(0));
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{error||<message>}} -- Causes a syntax error to "
                + "be reported with the specified message.";
    }
    
    public String getName()
    {
        return "error";
    }
    
}
