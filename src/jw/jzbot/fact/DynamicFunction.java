package jw.jzbot.fact;

import jw.jzbot.fact.ast.FactEntity;
import jw.jzbot.fact.exceptions.FactoidException;

public class DynamicFunction extends Function
{
    private String name;
    private FactEntity implementation;

    public DynamicFunction(String name, FactEntity implementation)
    {
        this.name = name;
        this.implementation = implementation;
    }
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext parentContext)
    {
        FactContext context = new FactContext(parentContext);
        context.setFunctionArguments(arguments);
        implementation.resolve(sink, context);
    }
    
    @Override
    public String getHelp(String topic)
    {
        throw new UnsupportedOperationException();
    }
    
}
