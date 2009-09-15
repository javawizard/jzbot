package org.opengroove.jzbot.fact.functions;

import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.Function;

public class FirstvarFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        for (int i = 1; i < arguments.length(); i++)
        {
            if (!arguments.get(i).trim().equals(""))
            {
                context.getLocalVars().put(arguments.get(0), arguments.get(i));
                break;
            }
        }
        return "";
    }
    
    @Override
    public String getName()
    {
        return "firstvar";
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{firstvar||<varname>||<firstarg>||<secondarg>||...}} -- "
                + "Sets the local variable <varname> to be the first argument that "
                + "comes after it whose value does not evaluate to the empty string,"
                + " ignoring whitespace and newlines.";
    }
    
}
