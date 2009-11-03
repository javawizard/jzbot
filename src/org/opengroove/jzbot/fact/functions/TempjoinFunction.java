package org.opengroove.jzbot.fact.functions;

import org.opengroove.jzbot.JZBot;
import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.Function;

public class TempjoinFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        // FIXME: increment sent message count
        JZBot.bot.joinChannel(arguments.get(0));
        return "";
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{tempjoin||<channel>}} -- Causes the bot to join the specified "
                + "channel, but without setting this channel to auto-join or creating "
                + "a factoid database for the channel.";
    }
    
}
