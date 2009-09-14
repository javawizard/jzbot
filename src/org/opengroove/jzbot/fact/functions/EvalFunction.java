package org.opengroove.jzbot.fact.functions;

import java.math.BigDecimal;
import java.text.DecimalFormatSymbols;

import org.cheffo.jeplite.JEP;
import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.FactoidException;
import org.opengroove.jzbot.fact.Function;

public class EvalFunction extends Function
{
    public static DecimalFormatSymbols symbols = new DecimalFormatSymbols();
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        String toEval = arguments.get(0);
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
            throw new FactoidException("Exception while running {{eval}} on "
                    + toEval, e);
        }
        if (value != value)
            return "Not a number";
        else if (value == Double.POSITIVE_INFINITY)
            return "Infinity";
        else if (value == Double.NEGATIVE_INFINITY)
            return "Negative infinity";
        return toRoundedString(value);
    }
    
    @Override
    public String getName()
    {
        return "eval";
    }
    
    /**
     * Returns a string representing this double rounded to 8 decimal points,
     * and with no decimal point if one is not needed.
     * 
     * @param value
     *            The value to round
     * @return The value
     */
    public static String toRoundedString(double value)
    {
        BigDecimal d = new BigDecimal(value);
        d = d.movePointRight(9);
        d = new BigDecimal(d.toBigInteger());
        d = d.movePointLeft(9);
        d = d.stripTrailingZeros();
        if (d.doubleValue() == 0)
            return "0";
        return d.toPlainString();
    }
    
    @Override
    public String getHelp(String topic)
    {
        // TODO Auto-generated method stub
        return null;
    }
}
