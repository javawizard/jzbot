package org.opengroove.jzbot.fact.functions;

import java.math.BigDecimal;

import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.Function;

public class RadixFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        int from = Integer.parseInt(arguments.get(0));
        int to = Integer.parseInt(arguments.get(1));
        long value = Long.parseLong(arguments.get(2), from);
        return Long.toString(value, to);
        
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{radix||<from>||<to>||<value>}} -- Converts <value>, which is a "
                + "number in base-<from>, to be a value in base-<to>. For example, "
                + "{{radix||10||16||12}} evaluates to \"b\", and {{radix||10||16||35}} "
                + "evaluates to \"23\". Fractional numbers are not currently allowed.";
    }
    
    @Override
    public String getName()
    {
        return "radix";
    }
    
}
