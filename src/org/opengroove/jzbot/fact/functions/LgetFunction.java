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
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{lget||<varname>}} -- Evaluates to the value of the specified local "
                + "variable. {{lget||something}} is equivalent to %something%. However, using "
                + "percent signs doesn't allow for dynamic variable names (as an "
                + "example, you couldn't do something like %param-%index%% for "
                + "syntax reasons), which is where you would use {{lget}}.";
    }
    
}
