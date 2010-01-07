package jw.jzbot.fact.functions;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class PFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {p|...} -- Calls some protocol-specific function. "
                + "What this does entirely depends on the protocol of the current scope's "
                + "server. For example, if the current server is a BZFlag server, then "
                + "{p|set|_skyColor|red} would cause the BZFlag world's sky color "
                + "to change to red, assuming the bot is a server administrator.\n"
                + "TODO: actually implement this";
    }
    
}
