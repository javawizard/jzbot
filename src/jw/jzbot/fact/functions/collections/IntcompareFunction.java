package jw.jzbot.fact.functions.collections;

import java.math.BigInteger;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import jw.jzbot.fact.functions.conditional.IfFunction;

public class IntcompareFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        String firstString = arguments.resolveString(0);
        String secondString = arguments.resolveString(1);
        boolean forIf = false;
        if (arguments.length() > 2)
        {
            forIf = IfFunction.findValue(arguments.resolveString(2));
        }
        BigInteger first = new BigInteger(firstString);
        BigInteger second = new BigInteger(secondString);
        int result = first.compareTo(second);
        result = (int) Math.signum(result);
        if (forIf && result == -1)
            result = 0;
        sink.write(result);
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {intcompare|<first>|<second>|<if>} -- Same as {compare}, "
                + "but compares whole numbers for numerical ordering. For example, "
                + "{intcompare|1|3} evaluates to -1, {intcompare|7|12} "
                + "evaluates to -1 (whereas {compare|7|12} would evaluate to 1), "
                + "and {intcompare|13|8} evaluates to 1. <if> is optional, and defaults "
                + "to false.";
    }
    
}
