package jw.jzbot.fact.functions;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;

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
