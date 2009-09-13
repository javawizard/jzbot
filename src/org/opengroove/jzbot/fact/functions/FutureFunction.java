package org.opengroove.jzbot.fact.functions;

import org.opengroove.jzbot.JZBot;
import org.opengroove.jzbot.JZBot.FutureFactoid;
import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.Function;

public class FutureFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        ArgumentList newArgs = arguments.subList(2);
        String key = arguments.get(0);
        int delay = Integer.parseInt(arguments.get(1));
        FutureFactoid future = new FutureFactoid(delay, context.getChannel(),
                newArgs, context.getSender(), key);
        synchronized (JZBot.futureFactoidLock)
        {
            JZBot.futureFactoids.put(key, future);
            future.start();
        }
    }
    
    @Override
    public String getName()
    {
        return "future";
    }
    
}
