package jw.jzbot.fact;

public class FunctionReference extends FactEntity
{
    private Sequence arguments;
    
    private String functionName;
    
    private boolean omitFromStack = false;
    
    public boolean isOmitFromStack()
    {
        return omitFromStack;
    }
    
    public void setOmitFromStack(boolean omitFromStack)
    {
        this.omitFromStack = omitFromStack;
    }
    
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
        try
        {
            return function.evaluate(sublist, context);
        }
        catch (FactoidException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new FactoidException("Internal error while running "
                    + functionName, e);
        }
    }
    
    @Override
    protected void addStackFrame(FactoidException e)
    {
        super.addStackFrame(e);
        if (omitFromStack)
            return;
        String functionName = this.functionName;
        if (functionName == null || functionName.equals(""))
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
