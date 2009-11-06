package jw.jzbot.fact.functions;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;

public class LowerFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        return arguments.get(0).toLowerCase();
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{lower||<value>}} -- Converts the specified value to lower case.";
    }
    
}
