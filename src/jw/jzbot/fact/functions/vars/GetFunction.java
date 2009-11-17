package jw.jzbot.fact.functions.vars;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;

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
    
    public String getName()
    {
        return "get";
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{get||<varname>}} -- Gets the value of the global "
                + "variable named <varname>.";
    }
    
}
