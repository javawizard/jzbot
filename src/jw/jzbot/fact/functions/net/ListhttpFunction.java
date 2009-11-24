package jw.jzbot.fact.functions.net;

import jw.jzbot.JZBot;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import net.sf.opengroove.common.utils.StringUtils;
import net.sf.opengroove.common.utils.StringUtils.ToString;


public class ListhttpFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
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
