package org.opengroove.jzbot.fact.functions;

import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.FactoidException;
import org.opengroove.jzbot.fact.Function;

public class NumberlistFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        int start = Integer.parseInt(arguments.get(0));
        int end = Integer.parseInt(arguments.get(1));
        int step = 1;
        if (arguments.length() > 2)
            step = Math.abs(Integer.parseInt(arguments.get(2)));
        if (end < start)
            step *= -1;
        StringBuffer buffer = new StringBuffer();
        boolean first = true;
        if (step == 0)
            throw new FactoidException("A <step> of 0 is not allowed.");
        if (start == end)
            return "" + start;
        while ((start <= end && step > 0) || (start >= end && step < 0))
        {
            if (first)
                first = false;
            else
                buffer.append(" ");
            buffer.append(start);
            start += step;
        }
        return buffer.toString();
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
                + "8||45||10}} evaluates to \"8 18 28 38\". This can be used with {{split}} "
                + "to create a for loop.";
    }
    
    public String getName()
    {
        return "numberlist";
    }
    
}
