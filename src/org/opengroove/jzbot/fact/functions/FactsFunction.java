package org.opengroove.jzbot.fact.functions;

import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.Function;

public class FactsFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        throw new UnsupportedOperationException(
                "The {{facts}} function isn't yet supported.");
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{facts||<scope>}} -- Returns a space-separated list of the "
                + "names of all factoids in the scope that this factoid is in. If <scope> "
                + "is \"channel\", and this is a global factoid, then this will list "
                + "channel-specific factoids at whatever channel this is being run at. "
                + "Otherwise, <scope> has no effect. <scope> is optional.";
    }
    
}
