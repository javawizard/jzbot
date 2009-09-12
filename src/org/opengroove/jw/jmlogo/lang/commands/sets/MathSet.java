package org.opengroove.jw.jmlogo.lang.commands.sets;

import java.util.Vector;

import henson.midp.Float11;

import org.opengroove.jw.jmlogo.lang.Command;
import org.opengroove.jw.jmlogo.lang.commands.math.OneArgMathCommand;
import org.opengroove.jw.jmlogo.lang.commands.math.TwoArgMathCommand;
import org.opengroove.jw.jmlogo.utils.Math2;

public class MathSet
{
    public static Command[] commands = loadCommands();
    
    private static Command[] loadCommands()
    {
        Vector list = new Vector();
        
        list.addElement(new OneArgMathCommand()
        {
            public double compute(double v)
            {
                return Math.sqrt(v);
            }
            
            public String getName()
            {
                return "sqrt";
            }
        });
        list.addElement(new OneArgMathCommand()
        {
            public double compute(double v)
            {
                return Math2.round(v);
            }
            
            public String getName()
            {
                return "round";
            }
        });
        list.addElement(new OneArgMathCommand()
        {
            public double compute(double v)
            {
                if (v > 0)
                    return 1;
                else if (v < 0)
                    return -1;
                else
                    return 0;
            }
            
            public String getName()
            {
                return "sign";
            }
        });
        list.addElement(new TwoArgMathCommand()
        {
            
            public double compute(double v1, double v2)
            {
                return Float11.pow(v1, v2);
            }
            
            public String getName()
            {
                return "power";
            }
        });
        list.addElement(new OneArgMathCommand()
        {
            
            public double compute(double v)
            {
                return Math.sin(Math.toRadians(v));
            }
            
            public String getName()
            {
                return "sin";
            }
        });
        list.addElement(new OneArgMathCommand()
        {
            
            public double compute(double v)
            {
                return Math.toDegrees(Float11.asin(v));
            }
            
            public String getName()
            {
                // TODO Auto-generated method stub
                return "arcsin";
            }
        });
        list.addElement(new OneArgMathCommand()
        {
            
            public double compute(double v)
            {
                return Math.tan(Math.toRadians(v));
            }
            
            public String getName()
            {
                // TODO Auto-generated method stub
                return "tan";
            }
        });
        list.addElement(new OneArgMathCommand()
        {
            
            public double compute(double v)
            {
                // TODO Auto-generated method stub
                return Math.toDegrees(Float11.atan(v));
            }
            
            public String getName()
            {
                // TODO Auto-generated method stub
                return "arctan";
            }
        });
        list.addElement(new TwoArgMathCommand()
        {
            
            public double compute(double v1, double v2)
            {
                return Math.toDegrees(Float11.atan2(v1, v2));
            }
            
            public String getName()
            {
                // TODO Auto-generated method stub
                return "arctan2";
            }
        });
        list.addElement(new OneArgMathCommand()
        {
            
            public double compute(double v)
            {
                return Math.toDegrees(v);
            }
            
            public String getName()
            {
                return "todegrees";
            }
        });
        list.addElement(new OneArgMathCommand()
        {
            
            public double compute(double v)
            {
                return Math.toRadians(v);
            }
            
            public String getName()
            {
                return "toradians";
            }
        });
        Command[] them = new Command[list.size()];
        list.copyInto(them);
        return them;
    }
}
