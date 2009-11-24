package jw.jzbot.fact.functions;

import java.math.BigInteger;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class IsinrangeFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        try
        {
            BigInteger number = new BigInteger(arguments.resolveString(0));
            BigInteger min = new BigInteger(arguments.resolveString(1));
            BigInteger max = new BigInteger(arguments.resolveString(2));
            if (number.compareTo(min) < 0)// <number> is less than <min>
                sink.write('0');
            else if (number.compareTo(max) > 0)// <number> is greater than <max>
                sink.write('0');
            // <number> is between <min> and <max>, inclusive
            else
                sink.write('1');
        }
        catch (Exception e)
        {
            e.printStackTrace();
            sink.write('0');
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
