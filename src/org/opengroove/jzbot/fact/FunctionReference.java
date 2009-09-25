package org.opengroove.jzbot.fact;

public class FunctionReference extends FactEntity
{
    private Sequence arguments;
    
    private String functionName;
    
    public FunctionReference(Sequence arguments)
    {
        this.arguments = arguments;
    }
    
    @Override
    public String execute(FactContext context)
    {
        ArgumentList list = new ArgumentList(arguments, context);
        functionName = list.get(0);
        Function function = FactParser.getFunction(functionName);
        if (function == null)
            throw new FactoidException("No such function: " + functionName);
        ArgumentList sublist = list.subList(1);
        return function.evaluate(sublist, context);
    }
    
    @Override
    protected void addStackFrame(FactoidException e)
    {
        super.addStackFrame(e);
        String functionName = this.functionName;
        if (functionName.equals(""))
            functionName = "(blank function reference)";
        e.addFrame(new FactoidStackFrame(super.getCharIndex(), super
                .getFactName(), functionName));
    }
    
    @Override
    public String explain(int indentation, int increment)
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append(spaces(indentation) + "function:\n");
        buffer.append(arguments.explain(indentation, increment, false));
        return buffer.toString();
    }
    
    public Sequence getArgumentSequence()
    {
        return arguments;
    }
}
