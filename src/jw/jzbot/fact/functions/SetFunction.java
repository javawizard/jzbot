package jw.jzbot.fact.functions;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class SetFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        context.getGlobalVars().put(arguments.resolveString(0), arguments.resolveString(1));
    }
    
    public String getName()
    {
        return "set";
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {set|<varname>|<value>} -- Sets the named global variable "
                + "to the specified value.";
    }
    
}
