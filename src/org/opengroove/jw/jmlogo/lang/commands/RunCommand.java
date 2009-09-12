package org.opengroove.jw.jmlogo.lang.commands;

import org.opengroove.jw.jmlogo.lang.Command;
import org.opengroove.jw.jmlogo.lang.InterpreterContext;
import org.opengroove.jw.jmlogo.lang.ListToken;
import org.opengroove.jw.jmlogo.lang.Token;

public class RunCommand extends Command
{
    
    public int getArgumentCount()
    {
        return 1;
    }
    
    public String getName()
    {
        return "run";
    }
    
    public Token run(InterpreterContext context, Token[] arguments)
    {
        validateList(arguments[0]);
        context.executeInParent((ListToken) arguments[0]);
        return null;
    }
    
}
