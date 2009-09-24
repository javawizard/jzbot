package org.opengroove.jzbot.fact.functions;

import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.Function;

public class TrimFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        return arguments.get(0).trim();
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{trim||<text>}} -- Removes all leading and trailing whitespace from <text>.";
    }
    
}
