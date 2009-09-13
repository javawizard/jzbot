package org.opengroove.jzbot.fact.functions;

import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.Function;

public class LgetFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        String var = context.getLocalVars().get(arguments.get(0));
        if (var == null)
            return "";
        return var;
    }
    
    @Override
    public String getName()
    {
        return "lget";
    }
    
}
