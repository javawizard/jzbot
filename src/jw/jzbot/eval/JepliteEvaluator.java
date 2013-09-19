package jw.jzbot.eval;

import jw.jzbot.Evaluator;
import jw.jzbot.JZBot;

import org.cheffo.jeplite.JEP;

public class JepliteEvaluator extends Evaluator
{
    
    public String evaluate(String toEval)
    {
        JEP jep = new JEP();
        jep.addStandardConstants();
        jep.addStandardFunctions();
        jep.parseExpression(toEval);
        double value;
        try
        {
            value = jep.getValue();
        }
        catch (Exception e)
        {
            throw new RuntimeException("ParseException from JEP", e);
        }
        if (value != value)
            return "Not a number";
        else if (value == Double.POSITIVE_INFINITY)
            return "Infinity";
        else if (value == Double.NEGATIVE_INFINITY)
            return "Negative infinity";
        return JZBot.toRoundedString(value);
    }
    
    @Override
    public String getName()
    {
        return "jeplite";
    }
}
