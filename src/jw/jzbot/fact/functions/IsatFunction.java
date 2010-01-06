package jw.jzbot.fact.functions;

import java.util.Arrays;

import jw.jzbot.ConnectionContext;
import jw.jzbot.JZBot;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class IsatFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        ConnectionContext real = JZBot.getRealConnection(context.getServer());
        if (Arrays.asList(real.getConnection().getChannels()).contains(
                arguments.resolveString(0)))
            sink.write('1');
        else
            sink.write('0');
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {isat|<channel>} -- Evaluates to 1 if the bot is currently "
                + "at the specified channel, or 0 if the bot is not currently joined "
                + "to the specified channel. This does not take into account whether "
                + "or not the bot is currently on the bot's auto-join list; it simply "
                + "takes into account what channels the IRC server would see the bot \n"
                + "as being on. To find out if a particular channel is on the bot's "
                + "auto-join list, use {isautojoin} instead.";
    }
    
}
