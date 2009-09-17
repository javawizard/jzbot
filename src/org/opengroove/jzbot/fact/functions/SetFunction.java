package org.opengroove.jzbot.fact.functions;

import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.Function;

public class SetFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        context.getGlobalVars().put(arguments.get(0), arguments.get(1));
        return "";
    }
    
    @Override
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
