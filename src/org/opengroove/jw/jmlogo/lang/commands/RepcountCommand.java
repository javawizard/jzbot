package org.opengroove.jw.jmlogo.lang.commands;

import org.opengroove.jw.jmlogo.lang.Command;
import org.opengroove.jw.jmlogo.lang.InterpreterContext;
import org.opengroove.jw.jmlogo.lang.InterpreterException;
import org.opengroove.jw.jmlogo.lang.Token;
import org.opengroove.jw.jmlogo.lang.Variable;

public class RepcountCommand extends Command
{
    
    public int getArgumentCount()
    {
        return 0;
    }
    
    public String getName()
    {
        return "repcount";
    }
    
    public Token run(InterpreterContext context, Token[] arguments)
    {
        Variable var = context.getVariableHolder("_internal_repcount");
        if (var == null || var.getContext() != context)
            throw new InterpreterException(
                "repcount can't be used outside of a repeat statement");
        return var.getValue();
    }
    
}
