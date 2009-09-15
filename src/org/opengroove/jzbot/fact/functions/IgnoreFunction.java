package org.opengroove.jzbot.fact.functions;

import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.Function;

public class IgnoreFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        arguments.get(0);
        return "";
    }
    
    @Override
    public String getName()
    {
        return "ignore";
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{ignore||<value>}} -- Evaluates <value>, but doesn't insert it into "
                + "the factoid. For example, \"Hello {{ignore||World}}\" would produce a factoid "
                + "that, when run, outputs \"Hello \", not \"Hello World\". This is most useful "
                + "for including comments in the factoid.";
    }
    
}
