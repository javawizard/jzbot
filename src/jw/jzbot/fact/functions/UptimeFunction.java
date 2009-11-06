package jw.jzbot.fact.functions;

import jw.jzbot.JZBot;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;

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
    
    public String getName()
    {
        return "uptime";
    }
    
}
