package jw.jzbot.fact.functions.collections;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import jw.jzbot.fact.exceptions.BreakException;
import jw.jzbot.fact.exceptions.ContinueException;

public class ContinueFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        if (arguments.length() == 0)
            throw new ContinueException("{break} function used");
        ContinueException exception =
                new ContinueException("{break|" + arguments.getString(0)
                    + "} function used");
        exception.level = Integer.parseInt(arguments.getString(0));
        throw exception;
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {continue} or {continue|<level>} -- Same as the {break} function, "
            + "but instead of breaking out of the specified loop, the loop acts as if its "
            + "action had just finished evaluating, and it moves onto the next "
            + "evaluation of its action.";
    }
    
}
