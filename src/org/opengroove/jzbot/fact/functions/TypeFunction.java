package org.opengroove.jzbot.fact.functions;

import org.opengroove.jzbot.JZBot;
import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.FactoidException;
import org.opengroove.jzbot.fact.Function;
import org.opengroove.jzbot.storage.Channel;

public class TypeFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        String factname = arguments.get(0);
        boolean isGlobal = JZBot.storage.getFactoid(factname) != null;
        boolean isChannel = false;
        if (context.getChannel() != null)
        {
            Channel c = JZBot.storage.getChannel(context.getChannel());
            if (c != null)
            {
                if (c.getFactoid(factname) != null)
                    isChannel = true;
            }
        }
        if (isGlobal && isChannel)
            return "both";
        if (isGlobal && !isChannel)
            return "global";
        if (isChannel && !isGlobal)
            return "channel";
        return "none";
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{type||<factoidname>}} -- Evaluates to the type of factoid that "
                + "<factoidname> is. This is either \"global\", \"channel\", or \"none\". "
                + "Global means that the factoid is a global factoid, channel means that the "
                + "factoid is a channel-specific factoid, and nonexistent means that the "
                + "factoid in question does not exist. This checks for channel-specific "
                + "factoids only on the current channel.\n"
                + "If there is both a channel-specific factoid and a global factoid with "
                + "the name <factname>, \"both\" will be returned.";
    }
    
}
