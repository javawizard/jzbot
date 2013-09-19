package jw.jzbot.fact.functions;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class FuturesFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        throw new UnsupportedOperationException("The futures function isn't yet supported.");
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {futures} -- Evaluates to a newline-separated list of "
                + "the keys of all future tasks scheduled with {future} that have "
                + "not yet been run.";
    }
    
}
