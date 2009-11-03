package org.opengroove.jzbot.fact.functions;

import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.Function;

public class PartFunction extends Function
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
        return "Syntax: {{part||<channel>}} -- Causes the bot to leave the specified "
                + "channel, and remove the channel from the bot's auto-join list. "
                + "Channel-specific factoids will still be remembered for the next "
                + "time that the bot is asked to join the channel. If you want the "
                + "bot to leave the channel without removing the channel from the \n"
                + "bot's auto-join list, use {{temppart}}.";
    }
    
}
