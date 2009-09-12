package org.opengroove.jw.jmlogo.lang.commands.math;

import org.opengroove.jw.jmlogo.lang.Command;
import org.opengroove.jw.jmlogo.lang.InterpreterContext;
import org.opengroove.jw.jmlogo.lang.Token;
import org.opengroove.jw.jmlogo.lang.WordToken;

public abstract class TwoArgMathCommand extends Command
{
    
    public int getArgumentCount()
    {
        return 2;
    }
    
    public Token run(InterpreterContext context, Token[] arguments)
    {
        validateWord(arguments[0]);
        validateWord(arguments[1]);
        double v1 = ((WordToken) arguments[0]).getNumeric();
        double v2 = ((WordToken) arguments[1]).getNumeric();
        return new WordToken(compute(v1, v2));
    }
    
    public abstract double compute(double v1, double v2);
    
}
