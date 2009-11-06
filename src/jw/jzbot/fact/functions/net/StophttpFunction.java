package jw.jzbot.fact.functions.net;

import jw.jzbot.JZBot;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;

public class StophttpFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        int port = Integer.parseInt(arguments.get(0));
        JZBot.stopHttpServer(port);
        return "";
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{stophttp||<port>}} -- Stops an HTTP server running on the "
                + "specified port. See {{starthttp}} for more information on what "
                + "HTTP servers are.";
    }
    
}
