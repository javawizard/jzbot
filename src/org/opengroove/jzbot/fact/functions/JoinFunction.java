package org.opengroove.jzbot.fact.functions;

import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.Function;

public class JoinFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{join||<channel>}} -- Causes the bot to join the specified "
                + "channel and add the specified channel to the bot's auto-join list. "
                + "This also creates a factoid database for the channel if one does "
                + "not already exist.";
    }
    
}
