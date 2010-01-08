package jw.jzbot.fact.functions.irc;

import jw.jzbot.JZBot;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class ServerFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        String scope;
        if (arguments.length() > 0)
            scope = arguments.resolveString(0);
        else
            scope = context.currentScope();
        String result = JZBot.extractRelativeServer(scope, null);
        if (result == null)
            result = "";
        sink.write(result);
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {server|<scope>} -- Extracts the server name from the specified "
                + "scope and evaluates to it. If there is no server in the specified scope, "
                + "evaluates to nothing. <scope> is optional, and if not present, {server} "
                + "functions as if it had been invoked as \"{server|{scope}\".";
    }
    
}
