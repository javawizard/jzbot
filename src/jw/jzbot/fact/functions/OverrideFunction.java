package jw.jzbot.fact.functions;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class OverrideFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        context.getLocalVars().put("__internal_override", "true");
        return "";
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{override}} -- Only useful in a factoid called by a regex. Indicates "
                + "that no other factoids should be run, including other regexes and factoids "
                + "matching the channel's trigger. This does not block commands from running.";
    }
    
    public String getName()
    {
        return "override";
    }
    
}
