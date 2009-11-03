package org.opengroove.jzbot.fact.functions;

import org.opengroove.jzbot.JZBot;
import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.Function;
import org.opengroove.jzbot.storage.Channel;

public class IsautojoinFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        Channel c = JZBot.storage.getChannel(arguments.get(0));
        if (c == null)
            return "0";
        return c.isSuspended() ? "0" : "1";
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{isautojoin||<channel>}} -- Evaluates to 1 if the bot is set "
                + "to auto-join the specified channel, or 0 if the bot is not set to "
                + "auto-join the specified channel. If you just want to see if the "
                + "bot is currently at a channel, not whether it is set to auto-join, "
                + "use {{isat}}. If you want to see if the bot has a factoid database\n"
                + "for the specified channel, use {{hasfactdb}}.";
    }
    
}
