package jw.jzbot.fact.functions.vars;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class CfromcacheFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        String varname = arguments.resolveString(0);
        String result = context.getChainVars().get(varname);
        if (result == null)
        {
            result = arguments.resolveString(1);
            context.getChainVars().put(varname, result);
        }
        sink.write(result);
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {cfromcache|<varname>|<value>} -- If there is a chain "
            + "variable named <varname>, evaluates to its value without evaluating "
            + "<value>. Otherwise, evaluates <value>, stores its value in the "
            + "chain variable <varname>, and evaluates to the value.";
    }
    
}
