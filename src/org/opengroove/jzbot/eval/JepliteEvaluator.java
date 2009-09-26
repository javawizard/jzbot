package org.opengroove.jzbot.eval;

import org.cheffo.jeplite.JEP;
import org.opengroove.jzbot.Evaluator;
import org.opengroove.jzbot.JZBot;

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
