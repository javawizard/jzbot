package org.opengroove.jw.jmlogo.lang.commands.math;

public class SumCommand extends MultiArgMathCommand
{
    
    public double compute(double[] values)
    {
        double result = 0;
        for (int i = 0; i < values.length; i++)
        {
            result += values[i];
        }
        return result;
    }
    
    public String getName()
    {
        return "sum";
    }
    
}
