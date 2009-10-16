package org.opengroove.jzbot.fact.functions;

import java.math.BigInteger;

import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.Function;

public class IsinrangeFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        try
        {
            BigInteger number = new BigInteger(arguments.get(0));
            BigInteger min = new BigInteger(arguments.get(1));
            BigInteger max = new BigInteger(arguments.get(2));
            if (number.compareTo(min) < 0)// <number> is less than <min>
                return "0";
            else if (number.compareTo(max) > 0)// <number> is greater than <max>
                return "0";
            // <number> is between <min> and <max>, inclusive
            return "1";
        }
        catch (Exception e)
        {
            /*
             * This will happen if the number isn't a number at all, and so can't be
             * parsed to a BigInteger
             */
            return "0";
        }
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{isinrange||<number>||<min>||<max>}} -- Checks to see if <number> is "
                + "greater than or equal to <min> and less than or equal to <max>. If "
                + "<number> is not within this range, or if <number> is not a whole "
                + "integer (or if it's not a number at all), then {{isinrange}} evaluates "
                + "to 0. Otherwise, {{isinrange}} evaluates to 1.";
    }
    
}
