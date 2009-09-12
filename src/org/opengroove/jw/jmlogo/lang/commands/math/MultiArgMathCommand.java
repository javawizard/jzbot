package org.opengroove.jw.jmlogo.lang.commands.math;

import org.opengroove.jw.jmlogo.lang.Command;
import org.opengroove.jw.jmlogo.lang.InterpreterContext;
import org.opengroove.jw.jmlogo.lang.Token;
import org.opengroove.jw.jmlogo.lang.WordToken;

public abstract class MultiArgMathCommand extends Command
{
    public int getMinArgs()
    {
        return 2;
    }
    
    public int getMaxArgs()
    {
        return 10000;
    }
    
    public abstract double compute(double[] args);
    
    public Token run(InterpreterContext context, Token[] arguments)
    {
        double[] values = new double[arguments.length];
        for (int i = 0; i < arguments.length; i++)
        {
            validateWord(arguments[i]);
            WordToken t = (WordToken) arguments[i];
            values[i] = t.getNumeric();
        }
        return new WordToken(compute(values));
    }
}
