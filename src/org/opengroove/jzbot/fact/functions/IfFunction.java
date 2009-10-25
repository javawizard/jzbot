package org.opengroove.jzbot.fact.functions;

import org.opengroove.jzbot.JZBot;
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
        Boolean result = findValueOrNull(arguments.get(0));
        if (result == null)
        {
            result = findValue(JZBot.evaluateEquation(arguments.get(0), context
                    .getChannel()));
        }
        if (result)
            return arguments.get(1);
        else
        {
            if (arguments.length() > 2)
                return arguments.get(2);
            else
                return "";
        }
    }
    
    public static boolean findValue(String value)
    {
        String toCompare = "|" + value.toLowerCase() + "|";
        if (yesValues.contains(toCompare))
            return true;
        else if (noValues.contains(toCompare))
            return false;
        else
            throw new FactoidException("Value to check was " + toCompare
                    + " when it should have been in " + yesValues + " or " + noValues);
    }
    
    public static Boolean findValueOrNull(String value)
    {
        String toCompare = "|" + value.toLowerCase() + "|";
        if (yesValues.contains(toCompare))
            return true;
        else if (noValues.contains(toCompare))
            return false;
        else
            return null;
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
                + "true value nor a false value, it is interpreted as an equation (as if "
                + "it were passed to {{eval}}), and then compared again.\n"
                + "The true values are y, yes, t, true, 1, and 1.0, and the false values "
                + "are n, no, f, false, 0, and 0.0. These values are case-insensitive. "
                + "<falseaction> is also optional.";
    }
    
}
