package jw.jzbot.fact.functions;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class HideFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{hide||<arg1>||<arg2>||...}} -- Does nothing, and evaluates to "
                + "nothing. Specifically, this does not evaluate any of its arguments, so, "
                + "for example, if you had an {{lset}} function call in one of the "
                + "arguments, it would not be run and the variable would not be set. This "
                + "function can be used to comment out some code in a factoid.";
    }
    
}
