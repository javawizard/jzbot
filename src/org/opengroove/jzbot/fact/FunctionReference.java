package org.opengroove.jzbot.fact;

public class FunctionReference extends FactEntity
{
    private Sequence arguments;
    
    public FunctionReference(Sequence arguments)
    {
        this.arguments = arguments;
    }
    
    @Override
    public String resolve(FactContext context)
    {
        ArgumentList list = new ArgumentList(arguments, context);
        String functionName = list.get(0);
        Function function = FactParser.getFunction(functionName);
        if (function == null)
            throw new FactoidException("No such function: " + functionName);
        ArgumentList sublist = list.subList(1);
        return function.evaluate(list, context);
    }
}
