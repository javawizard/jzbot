package org.opengroove.jzbot.fact.functions;

import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.Function;

public class UpperFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        return arguments.get(0).toUpperCase();
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{upper||<value>}} -- Converts the specified value to upper case.";
    }
    
}
