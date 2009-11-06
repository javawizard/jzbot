package jw.jzbot.fact.functions;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.FactoidException;
import jw.jzbot.fact.Function;

public class ErrorFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        throw new FactoidException("Factoid error: " + arguments.get(0));
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{error||<message>}} -- Causes a syntax error to "
                + "be reported with the specified message.";
    }
    
    public String getName()
    {
        return "error";
    }
    
}
