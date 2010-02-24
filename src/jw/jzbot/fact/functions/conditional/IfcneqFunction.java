package jw.jzbot.fact.functions.conditional;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class IfcneqFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        if (!arguments.resolveString(0).equals(arguments.resolveString(1)))
            arguments.resolve(2, sink);
        else if (arguments.length() > 3)
            arguments.resolve(3, sink);
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Exact same syntax and usage as {ifneq}, but this function is "
            + "case-sensitive when comparing values to see if they are equal, "
            + "whereas {ifneq} is case-insensitive.";
    }
    
}
