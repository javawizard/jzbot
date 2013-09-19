package jw.jzbot.fact.functions.collections;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import jw.jzbot.fact.exceptions.BreakException;
import jw.jzbot.fact.exceptions.ContinueException;
import jw.jzbot.fact.exceptions.FactoidException;
import jw.jzbot.fact.exceptions.NestedLoopException;
import jw.jzbot.fact.output.DelimitedSink;

public class ForFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        int start = Integer.parseInt(arguments.resolveString(0));
        int end = Integer.parseInt(arguments.resolveString(1));
        int step = 1;
        if (end < start)
            step = -1;
        String varname = arguments.resolveString(2);
        String delimiter = "";
        if (arguments.length() > 4)
            delimiter = arguments.resolveString(4);
        String previousValue = context.getLocalVars().get(varname);
        DelimitedSink result = new DelimitedSink(sink, delimiter);
        try
        {
            while ((start <= end && step > 0) || (start >= end && step < 0))
            {
                result.next();
                context.getLocalVars().put(varname, Integer.toString(start));
                try
                {
                    arguments.resolve(3, result);
                }
                catch (NestedLoopException e)
                {
                    e.level--;
                    if (e.level == -1)
                    {
                        if (e instanceof ContinueException)
                            continue;
                        else if (e instanceof BreakException)
                            break;
                    }
                    else
                    {
                        throw e;
                    }
                }
                start += step;
            }
        }
        finally
        {
            if (previousValue == null)
                context.getLocalVars().remove(varname);
            else
                context.getLocalVars().put(varname, previousValue);
        }
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {for|<from>|<to>|<varname>|<action>|<delimiter>} -- "
            + "Exactly the same as "
            + "{split| |{numberlist|<from>|<to>}|<varname>|<action>"
            + "|<delimiter>}, but this function runs around six times faster than "
            + "the split/numberlist combination function. Essentially, this function "
            + "runs <action> once for every number between <from> and <to>\n"
            + "with the local variable <varname> set to the current number. "
            + "The function then evaluates to all of the results of running <action> "
            + "with <delimiter> inbetween them.";
    }
    
}
