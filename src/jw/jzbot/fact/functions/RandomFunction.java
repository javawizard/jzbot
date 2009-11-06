package jw.jzbot.fact.functions;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;

public class RandomFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        return arguments.get((int) (Math.random() * arguments.length()));
    }
    
    public String getName()
    {
        return "random";
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{random||<choice1>||<choice2>||...}} -- Evaluates to one of "
                + "the choices at random. For example, "
                + "{{random||bye||see ya||laters||so long}} would evaluate to one of "
                + "\"bye\", \"see ya\", \"laters\", or \"so long\", chosen at random." +
                		"Only the choice that is chosen is evaluated.";
    }
    
}
