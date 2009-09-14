package org.opengroove.jzbot.fact.functions;

import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.Function;

public class IfneqFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        if (!arguments.get(0).equalsIgnoreCase(arguments.get(1)))
            return arguments.get(2);
        else if (arguments.length() > 3)
            return arguments.get(3);
        else
            return "";
    }
    
    @Override
    public String getName()
    {
        return "ifneq";
    }

    @Override
    public String getHelp(String topic)
    {
        // TODO Auto-generated method stub
        return null;
    }
}
