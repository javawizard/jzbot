package org.opengroove.jw.jmlogo.lang.commands.math;

import henson.midp.Float11;

public class RoundCommand extends OneArgMathCommand
{
    
    public String getName()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    public double compute(double v)
    {
        double d = v;
        d -= Math.floor(d);
        if (d < 0.5d)
            return Math.floor(v);
        else
            return Math.ceil(d);
    }
    
}
