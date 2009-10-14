package org.opengroove.jzbot.fact.functions;

import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.Function;

public class CgetFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        String var = context.getChainVars().get(arguments.get(0));
        if (var == null)
            return "";
        return var;
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{cget||<varname>}} -- Evaluates to the value of the "
                + "specified chain variable.";
    }
    
}
