package jw.jzbot.fact.functions;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;

public class SetFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        context.getGlobalVars().put(arguments.get(0), arguments.get(1));
        return "";
    }
    
    public String getName()
    {
        return "set";
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{set||<varname>||<value>}} -- Sets the named global variable "
                + "to the specified value.";
    }
    
}
