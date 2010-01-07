package jw.jzbot.fact.functions.vars;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class DeleteFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        context.getGlobalVars().remove(arguments.resolveString(0));
    }
    
    public String getName()
    {
        return "delete";
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {delete|<varname>} -- Deletes the global variable named <varname>.";
    }
    
}
