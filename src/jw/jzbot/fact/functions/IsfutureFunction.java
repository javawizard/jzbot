package jw.jzbot.fact.functions;

import jw.jzbot.JZBot;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class IsfutureFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        if (JZBot.futureFactoids.containsKey(arguments.resolveString(0)))
            sink.write('1');
        else
            sink.write('0');
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {isfuture|<key>} -- Evaluates to 1 if there is currently a "
                + "scheduled future task with the key <key> that has not run yet, "
                + "or 0 if there is no such scheduled future task.";
    }
    
    public String getName()
    {
        return "isfuture";
    }
    
}
