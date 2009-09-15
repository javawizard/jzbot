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
                newArgs, context.getSender(), key, context.getQuota());
        synchronized (JZBot.futureFactoidLock)
        {
            JZBot.futureFactoids.put(key, future);
            future.start();
        }
        return "";
    }
    
    @Override
    public String getName()
    {
        return "future";
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{future||<key>||<delay>||<factoid>||<argument1>||...}} -- "
                + "Schedules a factoid to be run some time in the future. <key> is a unique "
                + "key. Whenever an event is scheduled for a particular key, any events that "
                + "have been scheduled for that key but not yet run are canceled. <delay> "
                + "is the number of seconds in the future that the factoid should be run.\n"
                + "<factoid> is the name of the factoid to run. <argument1>, <argument2>, "
                + "etc. are arguments to pass to the factoid.";
    }
    
}
