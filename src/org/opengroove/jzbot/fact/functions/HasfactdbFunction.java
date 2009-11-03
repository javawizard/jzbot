package org.opengroove.jzbot.fact.functions;

import org.opengroove.jzbot.JZBot;
import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.Function;

public class HasfactdbFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        return JZBot.storage.getChannel(arguments.get(0)) == null ? "0" : "1";
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{hasfactdb||<channel>}} -- Evaluates to 1 if the specified "
                + "channel has a fact database, or 0 if the specified channel does not "
                + "have a fact database. A channel with a fact database is one at which "
                + "channel-specific factoids can be created and run. A channel acquires "
                + "a fact database when {{join}} is issued for that channel, but not \n"
                + "when {{tempjoin}} is issued for the channel. Currently, once a "
                + "channel has a fact database, that channel's fact database cannot be "
                + "removed. To see if a channel with a fact database is also an auto-join "
                + "channel, use {{isautojoin}}. To see if the bot is actually on a "
                + "channel, use {{isat}}.";
    }
    
}
