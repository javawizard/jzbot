package jw.jzbot.fact.functions.irc;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class VerifysuperopFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        context.getSender().verifySuperop();
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {verifysuperop}: Throws an exception if the user "
            + "invoking this factoid is not a superop. If the user is "
            + "a superop, this function silently evaluates to the empty string.";
    }
    
}
