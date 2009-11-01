package org.opengroove.jzbot.fact.functions;

import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.Function;

public class WhileFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        while ("1".equals(arguments.resolve(0)))
            arguments.resolve(1);
        return "";
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{while||<condition>||<action>}} -- Evaluates <condition>, and "
                + "if it's 1, evaluates <action> and then evaluates <condition again. This "
                + "keeps going on and on until <condition> evaluates to 0. {{while}} "
                + "doesn't evaluate to anything, even if its action did, so if you want any "
                + "output you'll need to use a variable to store it.";
    }
    
}
