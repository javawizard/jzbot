package org.opengroove.jzbot.fact.functions;

import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.Function;

public class OverrideFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
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
    
    @Override
    public String getName()
    {
        return "override";
    }
    
}
