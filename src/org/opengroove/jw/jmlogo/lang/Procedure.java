package org.opengroove.jw.jmlogo.lang;

/**
 * A logo procedure defined with TO. The interpreter is called to actually run
 * the contents of the command.
 * 
 * @author Alexander Boyd
 * 
 */
public class Procedure extends Command
{
    private String name;
    
    private String[] argnames;
    
    private ListToken instructions;
    
    private boolean isMacro;
    
    public Procedure(String name, String[] argnames, ListToken instructions,
        boolean isMacro)
    {
        this.name = name;
        this.argnames = argnames;
        this.instructions = instructions;
        this.isMacro = isMacro;
    }
    
    public int getArgumentCount()
    {
        return argnames.length;
    }
    
    public String getName()
    {
        return name;
    }
    
    public Token run(InterpreterContext context, Token[] arguments)
    {
        InterpreterContext subcontext =
            new InterpreterContext(context.getInterpreter(), context);
        for (int i = 0; i < arguments.length; i++)
        {
            subcontext.createLocalVariable(argnames[i], arguments[i]);
        }
        Result output;
        try
        {
            output =
                context.getInterpreter().evaluate(new TokenIterator(instructions),
                    subcontext);
        }
        catch (InterpreterException e)
        {
            StackFrame frame = new StackFrame();
            frame.setProcedure(name);
            e.addStackFrame(frame);
            throw e;
        }
        if(output == null)
            return null;
        if (output.getType() == Result.TYPE_IN_LINE)
            throw new InterpreterException("You don't say what to do with "
                + context.getInterpreter().toReadable(output.getValue(), 64));
        else if (output.getType() == Result.TYPE_STOP)
            return null;
        else if (output.getType() == Result.TYPE_OUTPUT)
        {
            if (isMacro)
            {
                if (!(output.getValue() instanceof ListToken))
                    throw new InterpreterException(
                        "Attempt to return a word from a macro is not allowed");
                context.executeInParent((ListToken) output.getValue());
                return null;
            }
            else
            {
                return output.getValue();
            }
        }
        return null;
    }
}
