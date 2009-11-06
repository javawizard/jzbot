package jw.jzbot.fact.functions;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;

public class UpperFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        return arguments.get(0).toUpperCase();
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{upper||<value>}} -- Converts the specified value to upper case.";
    }
    
}
