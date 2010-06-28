package jw.jzbot.fact;

import jw.jzbot.fact.exceptions.FactoidException;

public class DynamicFunction extends Function
{
    private String name;
    private FactContext context;
    
    public DynamicFunction(FactContext context, String name)
    {
        this.context = context;
        this.name = name;
    }
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        if (!context.equals(this.context))
            throw new FactoidException("Mismatched context while trying to invoke "
                + "a dynamic function. A dynamic function can only be invoked "
                + "using the context it was created from.");
        context.invokeDynamicFunction(name, sink, arguments);
    }
    
    @Override
    public String getHelp(String topic)
    {
        throw new UnsupportedOperationException();
    }
    
}
