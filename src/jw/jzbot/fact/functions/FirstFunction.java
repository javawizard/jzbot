package jw.jzbot.fact.functions;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.CheckedSink;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class FirstFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        for (int i = 0; i < arguments.length(); i++)
        {
            CheckedSink checked = new CheckedSink(sink);
            arguments.resolve(i,checked);
            if(checked.hasWritten())
                return;
        }
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {first|<arg1>|<arg2>|...} -- Evaluates to the first argument "
                + "that is not the empty string.";
    }
    
}
