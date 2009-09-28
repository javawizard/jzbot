package org.opengroove.jzbot.fact.functions;

import net.sf.opengroove.common.utils.StringUtils;
import net.sf.opengroove.common.utils.StringUtils.ToString;

import org.opengroove.jzbot.JZBot;
import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.Function;

public class ListhttpFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        return StringUtils.delimited(JZBot.httpServers.keySet().toArray(
                new Integer[0]), new ToString<Integer>()
        {
            
            @Override
            public String toString(Integer object)
            {
                return "" + object;
            }
        }, "|");
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{listhttp}} -- Returns a pipe-separated list of server "
                + "port numbers representing all currently-running HTTP servers. See "
                + "{{starthttp}} for information on what HTTP servers are.";
    }
    
}
