package jw.jzbot.fact.functions.net;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;

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
