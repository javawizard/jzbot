package jw.jzbot.fact.functions;

import jw.jzbot.JZBot;
import jw.jzbot.JZBot.FutureFactoid;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class RemainingFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        FutureFactoid fact = JZBot.futureFactoids.get(arguments.get(0));
        if (fact == null)
            return "";
        return "" + Math.max(fact.startTime - System.currentTimeMillis(), 0);
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{remaining||<key>}} -- Evaluates to the number of milliseconds "
                + "left before the future whose key is <key> will run, or the empty string "
                + "if there is no future with the specified key.";
    }
    
}
