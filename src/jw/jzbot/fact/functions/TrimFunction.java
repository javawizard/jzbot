package jw.jzbot.fact.functions;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;

public class TrimFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        return arguments.get(0).trim();
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{trim||<text>}} -- Removes all leading and trailing whitespace from <text>.";
    }
    
}
