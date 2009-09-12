package org.opengroove.jw.jmlogo.lang.commands.math;

import org.opengroove.jw.jmlogo.lang.Command;
import org.opengroove.jw.jmlogo.lang.InterpreterContext;
import org.opengroove.jw.jmlogo.lang.Token;
import org.opengroove.jw.jmlogo.lang.WordToken;

public abstract class OneArgMathCommand extends Command
{
    
    public int getArgumentCount()
    {
        return 1;
    }
    
    public Token run(InterpreterContext context, Token[] arguments)
    {
        validateWord(arguments[0]);
        return new WordToken(compute(((WordToken) arguments[0]).getNumeric()));
    }
    
    public abstract double compute(double v);
    
}
