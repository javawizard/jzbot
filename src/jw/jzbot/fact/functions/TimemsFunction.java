package jw.jzbot.fact.functions;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class TimemsFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        sink.write(Long.toString(System.currentTimeMillis()));
    }
    
    public String getName()
    {
        return "timems";
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {timems} -- Returns the server's current time in milliseconds since "
                + "January 1, 1970 Midnight UTC.";
    }
    
}
