package jw.jzbot.fact.functions.vars;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class LgetFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        String var = context.getLocalVars().get(arguments.resolveString(0));
        if (var != null)
            sink.write(var);
    }
    
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
                + "syntax reasons), which is where this function comes in useful.";
    }
    
}
