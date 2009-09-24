package org.opengroove.jzbot.fact.functions;

import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.Function;

public class LowerFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        return arguments.get(0).toLowerCase();
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{lower||<value>}} -- Converts the specified value to lower case.";
    }
    
}
