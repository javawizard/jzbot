package org.opengroove.jzbot.fact.functions;

import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.Function;

public class MatchFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        return arguments.get(1).matches(arguments.get(0)) ? "1" : "0";
    }
    
    public String getName()
    {
        return "match";
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{match||<regex>||<test>}} -- Evaluates to 1 if <test> matches the "
                + "regular expression <regex>, or 0 if it does not.";
    }
    
}
