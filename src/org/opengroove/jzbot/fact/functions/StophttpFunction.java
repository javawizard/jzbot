package org.opengroove.jzbot.fact.functions;

import org.opengroove.jzbot.JZBot;
import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.Function;

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
