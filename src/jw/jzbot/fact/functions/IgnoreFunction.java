package jw.jzbot.fact.functions;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;

public class IgnoreFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        arguments.get(0);
        return "";
    }
    
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
