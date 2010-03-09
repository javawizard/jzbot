package jw.jzbot.fact.functions;

import jw.jzbot.JZBot;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class TemppartFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        context.incrementMessageCount();
        String target = arguments.resolveString(0);
        JZBot.checkedGetExtractedConnection(target, context).partChannel(
                JZBot.extractRelativeChannel(target, context),
                (arguments.length() > 1 ? arguments.resolveString(1) : JZBot.PART_MESSAGE));
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {temppart|<channel>|<reason>} -- Causes the bot to leave "
                + "the specified channel, but without removing the channel from the bot's "
                + "auto-join list. If <reason>, which is optional, is specified, then "
                + "that reason is used to leave the channel.";
    }
    
}
