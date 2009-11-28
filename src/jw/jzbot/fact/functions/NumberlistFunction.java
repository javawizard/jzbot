package jw.jzbot.fact.functions;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.BufferedSink;
import jw.jzbot.fact.DelimitedSink;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.FactoidException;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class NumberlistFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        int start = Integer.parseInt(arguments.resolveString(0));
        int end = Integer.parseInt(arguments.resolveString(1));
        int step = 1;
        if (arguments.length() > 2)
            step = Math.abs(Integer.parseInt(arguments.resolveString(2)));
        if (end < start)
            step *= -1;
        DelimitedSink result = new DelimitedSink(sink, " ");
        if (step == 0)
            throw new FactoidException("A <step> of 0 is not allowed.");
        if (start == end)
        {
            sink.write(start);
            return;
        }
        while ((start <= end && step > 0) || (start >= end && step < 0))
        {
            result.next();
            result.write(start);
            start += step;
        }
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{numberlist||<start>||<end>||<step>}} -- Evaluates to a "
                + "space-separated list of the whole integers starting with <start> "
                + "and ending with <end>. <step> is optional, and specifies if numbers "
                + "should be skipped. Whether or not <step> is positive or negative has "
                + "no effect on the ordering of numbers; which one (of start and end) "
                + "is above the other is what determines ordering.\n"
                + "For example, {{numberlist||1||5}} evaluates to \"1 2 3 4 5\", "
                + "{{numberlist||5||1}} evaluates to \"5 4 3 2 1\", {{numberlist||3||3}} "
                + "evaluates to \"3\", {{numberlist||45||8||10}} evaluates to \"45 35 25 15\", "
                + "{{numberlist||8||45||10}} evaluates to \"8 18 28 38\", and {{numberlist||"
                + "8||45||-10}} evaluates to \"8 18 28 38\". This can be used with {{split}} "
                + "to create a for loop.";
    }
    
    public String getName()
    {
        return "numberlist";
    }
    
}
