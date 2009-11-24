package jw.jzbot.fact.functions;

import jw.jzbot.JZBot;
import jw.jzbot.JZBot.FutureFactoid;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class FutureFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        ArgumentList newArgs = arguments.subList(2);
        String key = arguments.getString(0);
        int delay = Integer.parseInt(arguments.getString(1));
        FutureFactoid future = new FutureFactoid(delay, context.getChannel(),
                newArgs, context.getSender(), key, context.getQuota());
        synchronized (JZBot.futureFactoidLock)
        {
            JZBot.futureFactoids.put(key, future);
            future.start();
        }
    }
    
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
