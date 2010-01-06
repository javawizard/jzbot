package jw.jzbot.fact.functions;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class FactoverrideFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        context.getLocalVars().put("__fact_override", "true");
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {factoverride} -- Only useful in a factoid called by a regex. Indicates "
                + "that no other factoids should be run after this regex. This differs from "
                + "{override} in that other regexes will still be run, but actual factoids "
                + "will not. As with {override}, this does not block commands from running.";
    }
    
    public String getName()
    {
        return "factoverride";
    }
    
}
