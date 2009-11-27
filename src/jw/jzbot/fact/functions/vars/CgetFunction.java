package jw.jzbot.fact.functions.vars;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class CgetFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        String var = context.getChainVars().get(arguments.get(0));
        if (var != null)
            sink.write(var);
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{cget||<varname>}} -- Evaluates to the value of the "
                + "specified chain variable.";
    }
    
}
