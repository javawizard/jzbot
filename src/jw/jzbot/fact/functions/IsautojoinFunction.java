package jw.jzbot.fact.functions;

import jw.jzbot.JZBot;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import jw.jzbot.storage.Channel;
import jw.jzbot.storage.Server;

public class IsautojoinFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        Server server = JZBot.storage.getServer(context.getServer());
        Channel c = server.getChannel(arguments.resolveString(0));
        if (c == null)
            sink.write('0');
        else if (c.isSuspended())
            sink.write('0');
        else
            sink.write('1');
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
