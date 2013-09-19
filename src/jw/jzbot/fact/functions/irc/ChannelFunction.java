package jw.jzbot.fact.functions.irc;

import jw.jzbot.JZBot;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class ChannelFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        String scope;
        if (arguments.length() > 0)
            scope = arguments.resolveString(0);
        else
            scope = context.currentScope();
        String result = JZBot.extractRelativeChannel(scope, null);
        if (result == null)
            result = "";
        sink.write(result);
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {channel|<scope>} -- Exactly the same as {server|<scope>}, but "
                + "extracts the channel part instead of the server part.";
    }
    
}
