package jw.jzbot.fact.functions.irc;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class UsernameFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        if(context.getSender().getUsername() != null)
            sink.write(context.getSender().getUsername());
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {username} -- Evaluates to the username of the "
            + "user running the factoid that this is being called from. "
            + "If the username is not known, this function evaluates to "
            + "the empty string.";
    }
    
}
