package jw.jzbot.fact.functions;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.CustomFactoidException;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.FactoidException;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class ErrorFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        throw new CustomFactoidException(arguments.getString(0));
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {error|<message>} -- Causes a syntax error to "
                + "be reported with the specified message. If the error is later caught "
                + "with {catch}, the exception class will be "
                + "jw.jzbot.fact.CustomFactoidException and the message will be <message>.";
    }
    
    public String getName()
    {
        return "error";
    }
    
}
