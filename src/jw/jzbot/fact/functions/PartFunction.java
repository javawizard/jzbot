package jw.jzbot.fact.functions;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class PartFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
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
