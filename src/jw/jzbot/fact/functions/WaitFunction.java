package jw.jzbot.fact.functions;

import jw.jzbot.TimedKillThread;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;

public class WaitFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        try
        {
            Thread.sleep(Integer.parseInt(arguments.get(0)));
        }
        catch (InterruptedException e)
        {
        }
        return "";
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{wait||<duration>}} -- Waits <duration> milliseconds before continuing. "
                + "The bot has a built-in timer that won't let a factoid run longer than "
                + (TimedKillThread.MAX_FACT_RUN_TIME / 1000)
                + " seconds, so don't make <duration> longer than that.";
    }
    
}
