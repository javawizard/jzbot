package org.opengroove.jzbot.fact.functions;

import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.FactoidException;
import org.opengroove.jzbot.fact.Function;

public class ReplaceFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        String mode = "regex";
        if (arguments.length() == 4)
        {
            mode = arguments.get(0);
            arguments = arguments.subList(1);
        }
        if (mode.equals("regex"))
        {
            return arguments.get(0).replaceAll(arguments.get(1),
                    arguments.get(2));
        }
        else if (mode.equals("text"))
        {
            return arguments.get(0).replace(arguments.get(1), arguments.get(2));
        }
        else
            throw new FactoidException("Mode to {{replace}} was " + mode
                    + ", not regex or text");
    }
    
    @Override
    public String getName()
    {
        return "replace";
    }
    
}
