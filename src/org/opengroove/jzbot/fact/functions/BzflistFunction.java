package org.opengroove.jzbot.fact.functions;

import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.Function;

public class BzflistFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "NOT IMPLEMENTED YET. When this is implemented, it will request " +
        		"various stats from my.bzflag.org, and ultimately return stuff " +
        		"on what servers are running. {{bzfplayer}} returns info about " +
        		"a specific player. Also, this might use strayer's bzstats " +
        		"instead, if I can get his permission.";
    }
    
}
