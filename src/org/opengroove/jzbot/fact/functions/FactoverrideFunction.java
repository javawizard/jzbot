package org.opengroove.jzbot.fact.functions;

import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.Function;

public class FactoverrideFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        context.getLocalVars().put("__fact_override", "true");
        return "";
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{factoverride}} -- Only useful in a factoid called by a regex. Indicates "
                + "that no other factoids should be run after this regex. This differs from "
                + "{{override}} in that other regexes will still be run, but actual factoids "
                + "will not. As with {{override}}, this does not block commands from running.";
    }
    
    public String getName()
    {
        return "factoverride";
    }
    
}
