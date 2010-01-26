package jw.jzbot.fact.functions;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import jw.jzbot.fact.exceptions.BreakException;
import jw.jzbot.fact.exceptions.NestedLoopException;
import jw.jzbot.fact.output.NullSink;

public class WhileFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        while ("1".equals(arguments.resolveString(0)))
        {
            try
            {
                arguments.resolve(1, NullSink.sink);
            }
            catch (NestedLoopException e)
            {
                e.level--;
                if (e.level == -1)
                {
                    if (e instanceof BreakException)
                        break;
                    else
                        continue;
                }
                else
                    throw e;
            }
        }
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {while|<condition>|<action>} -- Evaluates <condition>, and "
            + "if it's 1, evaluates <action> and then evaluates <condition> again. This "
            + "keeps going on and on until <condition> evaluates to 0. {while} "
            + "doesn't evaluate to anything, even if its action did, so if you want any "
            + "output you'll need to use a variable to store it. Note that {continue} and "
            + "{break} functions used within the condition act as if they had been used "
            + "outside of the while function, IE only {break} and {continue} statements "
            + "placed within the action itself cause the while loop to continue or break.";
    }
    
}
