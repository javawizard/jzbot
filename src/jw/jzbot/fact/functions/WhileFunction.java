package jw.jzbot.fact.functions;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.NullSink;
import jw.jzbot.fact.Sink;

public class WhileFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        while ("1".equals(arguments.resolveString(0)))
            arguments.resolve(1, NullSink.sink);
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{while||<condition>||<action>}} -- Evaluates <condition>, and "
                + "if it's 1, evaluates <action> and then evaluates <condition> again. This "
                + "keeps going on and on until <condition> evaluates to 0. {{while}} "
                + "doesn't evaluate to anything, even if its action did, so if you want any "
                + "output you'll need to use a variable to store it.";
    }
    
}
