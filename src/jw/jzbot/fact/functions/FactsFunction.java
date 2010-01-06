package jw.jzbot.fact.functions;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class FactsFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        throw new UnsupportedOperationException(
                "The {facts} function isn't yet supported.");
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {facts|<scope>} -- Returns a space-separated list of the "
                + "names of all factoids in the scope that this factoid is in. If <scope> "
                + "is \"channel\", and this is a global factoid, then this will list "
                + "channel-specific factoids at whatever channel this is being run at. "
                + "Otherwise, <scope> has no effect. <scope> is optional.";
    }
    
}
