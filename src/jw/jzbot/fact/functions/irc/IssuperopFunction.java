package jw.jzbot.fact.functions.irc;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class IssuperopFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        sink.write(context.getSender().isSuperop() ? 1 : 0);
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {issuperop}: Evaluates to 1 if the user invoking "
            + "this factoid is a superop, 0 if they are not.";
    }
    
}
