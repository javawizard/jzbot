package jw.jzbot.fact.functions.vars;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class GetFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        String var = context.getGlobalVars().get(arguments.resolveString(0));
        if (var != null)
            sink.write(var);
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
