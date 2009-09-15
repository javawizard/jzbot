package org.opengroove.jzbot.fact.functions;

import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.Function;

public class DeleteFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        context.getGlobalVars().remove(arguments.get(0));
        return "";
    }
    
    @Override
    public String getName()
    {
        return "delete";
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{delete||<varname>}} -- Deletes the global variable named <varname>.";
    }
    
}
