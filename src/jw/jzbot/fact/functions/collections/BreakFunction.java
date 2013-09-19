package jw.jzbot.fact.functions.collections;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import jw.jzbot.fact.exceptions.BreakException;

public class BreakFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        if (arguments.length() == 0)
            throw new BreakException("{break} function used");
        BreakException exception =
                new BreakException("{break|" + arguments.getString(0) + "} function used");
        exception.level = Integer.parseInt(arguments.getString(0));
        throw exception;
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {break} or {break|<level>} -- Causes a number of loop "
            + "functions that support it to drop out of the loop. This is "
            + "currently only supported by {sqlfor}; I need to add support "
            + "for it to {while}, {split}, {for}, {bzflist}, and any others "
            + "that I might have missed. <level> is the nesting level to break "
            + "out of; This can be used when a loop is nested within a loop and some "
            + "code in the innermost loop needs to break out of the outermost "
            + "loop. {break} is equivalent to {break|0}, IE it breaks out of the "
            + "innermost loop. {break|1} would break out of the loop surrounding "
            + "the innermost loop, {break|2} would break out of the loop surrounding "
            + "that one and so on. Currently, this function uses a specially-formed "
            + "error/exception, so a {catch} function call will catch the BreakException "
            + "and prevent this function from working properly.";
    }
    
}
