package org.opengroove.jzbot.fact.functions;

import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.Function;

public class RandomFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        return arguments.get((int) (Math.random() * arguments.length()));
    }
    
    @Override
    public String getName()
    {
        return "random";
    }
    
}
