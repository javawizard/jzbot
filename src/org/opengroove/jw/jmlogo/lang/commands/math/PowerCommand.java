package org.opengroove.jw.jmlogo.lang.commands.math;

import henson.midp.Float11;

public class PowerCommand extends TwoArgMathCommand
{
    
    public double compute(double v1, double v2)
    {
        return Float11.pow(v1, v2);
    }
    
    public String getName()
    {
        return "power";
    }
    
}
