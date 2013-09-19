package jw.jzbot.fact.functions;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class MatchFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        sink.write(arguments.resolveString(1).matches(arguments.resolveString(0)) ? '1'
                : '0');
    }
    
    public String getName()
    {
        return "match";
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {match|<regex>|<test>} -- Evaluates to 1 if <test> matches the "
                + "regular expression <regex>, or 0 if it does not.";
    }
    
}
