package jw.jzbot.fact.functions.conditional;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class IfneqFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        if (!arguments.resolveString(0).equalsIgnoreCase(arguments.resolveString(1)))
            arguments.resolve(2, sink);
        else if (arguments.length() > 3)
            arguments.resolve(3, sink);
    }
    
    public String getName()
    {
        return "ifneq";
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {ifneq|<compare1>|<compare2>|<trueaction>|<falseaction>} -- "
                + "Evaluates to <trueaction> if <compare1> is not the same as <compare2>, ignoring "
                + "case, or <falseaction> if they are equal. <falseaction> is optional, "
                + "and if not present {ifneq} will act as if <falseaction> were empty. This "
                + "function is essentially the opposite of {ifeq}.";
    }
}
