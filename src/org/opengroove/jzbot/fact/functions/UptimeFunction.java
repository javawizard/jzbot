package org.opengroove.jzbot.fact.functions;

import org.opengroove.jzbot.JZBot;
import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.Function;

public class UptimeFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        return "" + JZBot.startedAtTime;
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{uptime}} -- Returns the time, in milliseconds since Midnight "
                + "January 1, 1970, at which the bot started up.";
    }
    
    @Override
    public String getName()
    {
        return "uptime";
    }
    
}
