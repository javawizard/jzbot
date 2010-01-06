package jw.jzbot.fact.functions;

import jw.jzbot.JZBot;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.FactoidException;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import jw.jzbot.storage.Server;

public class HasfactdbFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        String channel = arguments.resolveString(0);
        String server = context.getServer();
        if (channel.startsWith("@"))
        {
            server = JZBot.extractServerName(channel);
            channel = JZBot.extractChannelName(channel);
        }
        Server s = JZBot.storage.getServer(server);
        if (s == null)
            throw new FactoidException("Invalid server name " + server + " (with channel "
                    + channel + ")");
        if (s.getChannel(channel) == null)
            sink.write('0');
        else
            sink.write('1');
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {hasfactdb|<channel>} -- Evaluates to 1 if the specified "
                + "channel has a fact database, or 0 if the specified channel does not "
                + "have a fact database. A channel with a fact database is one at which "
                + "channel-specific factoids can be created and run. A channel acquires "
                + "a fact database when {join} is issued for that channel, but not \n"
                + "when {tempjoi}} is issued for the channel. Currently, once a "
                + "channel has a fact database, that channel's fact database cannot be "
                + "removed. To see if a channel with a fact database is also an auto-join "
                + "channel, use {isautojoin}. To see if the bot is actually on a "
                + "channel, use {isat}.";
    }
    
}
