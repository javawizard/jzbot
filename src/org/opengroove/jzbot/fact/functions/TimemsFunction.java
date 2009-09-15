package org.opengroove.jzbot.fact.functions;

import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.Function;

public class TimemsFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        return "" + System.currentTimeMillis();
    }
    
    @Override
    public String getName()
    {
        return "timems";
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{timems}} -- Returns the server's current time in milliseconds since "
                + "January 1, 1970 Midnight UTC.";
    }
    
}
