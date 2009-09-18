package org.opengroove.jzbot.fact.functions;

import org.opengroove.jzbot.JZBot;
import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.Function;

public class IsfutureFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        return JZBot.futureFactoids.containsKey(arguments.get(0)) ? "1" : "0";
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{isfuture||<key>}} -- Evaluates to 1 if there is currently a "
                + "scheduled future task with the key <key> that has not run yet, "
                + "or 0 if there is no such scheduled future task.";
    }
    
    @Override
    public String getName()
    {
        return "isfuture";
    }
    
}
