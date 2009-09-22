package org.opengroove.jzbot.fact.functions;

import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.FactoidException;
import org.opengroove.jzbot.fact.Function;

public class IfFunction extends Function
{
    private static final String yesValues = "|y|yes|t|true|1|1.0|";
    private static final String noValues = "|n|no|f|false|0|0.0|";
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        String toCompare = "|" + arguments.get(0).toLowerCase() + "|";
        if (yesValues.contains(toCompare))
            return arguments.get(1);
        else if (noValues.contains(toCompare))
        {
            if (arguments.length() > 2)
                return arguments.get(2);
            else
                return "";
        }
        else
            throw new FactoidException("Value to if was " + toCompare
                    + " when it should have been in " + yesValues + " or "
                    + noValues);
    }
    
    public String getName()
    {
        return "if";
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{if||<condition>||<trueaction>||<falseaction>}} -- Evalutes to "
                + "<trueaction> if <condition> is one of the \"true\" values, and <falseaction> "
                + "if <condition> is one of the \"false\" values. If <condition> is neither a "
                + "true value nor a false value, an error occurs.\n"
                + "The true values are y, yes, t, true, 1, and 1.0, and the false values "
                + "are n, no, f, false, 0, and 0.0. These values are case-insensitive. "
                + "<falseaction> is also optional.";
    }
    
}
