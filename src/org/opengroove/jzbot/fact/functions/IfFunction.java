package org.opengroove.jzbot.fact.functions;

import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.FactoidException;
import org.opengroove.jzbot.fact.Function;

public class IfFunction extends Function
{
    private static final String yesValues = "|y|yes|true|TRUE|1|1.0|";
    private static final String noValues = "|n|no|false|FALSE|0|0.0|";
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        String toCompare = "|" + arguments.get(0) + "|";
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
    
    @Override
    public String getName()
    {
        return "if";
    }

    @Override
    public String getHelp(String topic)
    {
        // TODO Auto-generated method stub
        return null;
    }
    
}
