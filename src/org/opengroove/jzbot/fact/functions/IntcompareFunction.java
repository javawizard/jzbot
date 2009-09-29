package org.opengroove.jzbot.fact.functions;

import java.math.BigInteger;

import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.Function;

public class IntcompareFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        String firstString = arguments.get(0);
        String secondString = arguments.get(1);
        boolean forIf = false;
        if (arguments.length() > 2)
        {
            forIf = IfFunction.findValue(arguments.get(2));
        }
        BigInteger first = new BigInteger(firstString);
        BigInteger second = new BigInteger(secondString);
        int result = first.compareTo(second);
        result = (int) Math.signum(result);
        if (forIf && result == -1)
            result = 0;
        return "" + result;
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{intcompare||<first>||<second>||<if>}} -- Same as {{compare}}, "
                + "but compares whole numbers for numerical ordering. For example, "
                + "{{intcompare||1||3}} evaluates to -1, {{intcompare||7||12}} "
                + "evaluates to -1 (whereas {{compare||7||12}} would evaluate to 1), "
                + "and {{intcompare||13||8}} evaluates to 1. <if> is optional, and defaults "
                + "to false.";
    }
    
}
