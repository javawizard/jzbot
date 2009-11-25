package jw.jzbot.fact.functions;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class TrimFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        sink.write(arguments.resolveString(0).trim());
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{trim||<text>}} -- Removes all leading and trailing whitespace from <text>.";
    }
    
}
