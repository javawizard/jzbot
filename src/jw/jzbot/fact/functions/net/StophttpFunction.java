package jw.jzbot.fact.functions.net;

import jw.jzbot.JZBot;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class StophttpFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        int port = Integer.parseInt(arguments.resolveString(0));
        JZBot.stopHttpServer(port);
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{stophttp||<port>}} -- Stops an HTTP server running on the "
                + "specified port. See {{starthttp}} for more information on what "
                + "HTTP servers are.";
    }
    
}
