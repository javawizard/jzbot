package org.opengroove.jzbot.fact.functions;

import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.Function;

public class BzfqueryFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "NOT IMPLEMENTED YET. When this is implemented, it will connect "
                + "to the specified bzflag server and pull some stats from it, "
                + "such as the list of players that are there, the current team "
                + "scores, and so on.";
    }
    
}
