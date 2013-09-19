package jw.jzbot.fact.functions.varset;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class DecrementFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        IncrementFunction.modDown(context, arguments.resolveString(0));
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {decrement|<varname>} -- Gets the numeric value in the specified "
                + "local variable, subtracts one from it, and sets it back into the local variable. "
                + "If the specified local variable does not exist, this function will instead "
                + "set it to -1. If the specified local variable does exist but is not a "
                + "number (or if it exists but is the empty string), an error will occur.";
    }
    
}
