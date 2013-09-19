package jw.jzbot.fact.functions;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class JoinFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {join|<channel>} -- Causes the bot to join the specified "
                + "channel and add the specified channel to the bot's auto-join list. "
                + "This also creates a factoid database for the channel if one does "
                + "not already exist.";
    }
    
}
