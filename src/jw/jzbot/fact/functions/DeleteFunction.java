package jw.jzbot.fact.functions;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;

public class DeleteFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        context.getGlobalVars().remove(arguments.get(0));
        return "";
    }
    
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
