package org.opengroove.jzbot.fact.functions;

import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.Function;

public class GetFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        String var = context.getGlobalVars().get(arguments.get(0));
        if (var == null)
            return "";
        return var;
    }
    
    @Override
    public String getName()
    {
        return "get";
    }
    
}
