package org.opengroove.jzbot.fact.functions;

import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.Function;

public class IfeqFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        if (arguments.get(0).equalsIgnoreCase(arguments.get(1)))
            return arguments.get(2);
        else if (arguments.length() > 3)
            return arguments.get(3);
        else
            return "";
    }
    
    @Override
    public String getName()
    {
        return "ifeq";
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{ifeq||<compare1>||<compare2>||<trueaction>||<falseaction>}} -- "
                + "Evaluates to <trueaction> if <compare1> is the same as <compare2>, ignoring "
                + "case, or <falseaction> if they are not equal. <falseaction> is optional, "
                + "and if not present {{ifeq}} will act as if <falseaction> were empty. This "
                + "function is essentially the opposite of {{ifneq}}.";
    }
}
