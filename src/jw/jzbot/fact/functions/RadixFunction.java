package jw.jzbot.fact.functions;

import java.math.BigDecimal;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class RadixFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        int from = Integer.parseInt(arguments.resolveString(0));
        int to = Integer.parseInt(arguments.resolveString(1));
        long value = Long.parseLong(arguments.resolveString(2), from);
        sink.write(Long.toString(value, to));
        
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{radix||<from>||<to>||<value>}} -- Converts <value>, which is a "
                + "number in base-<from>, to be a value in base-<to>. For example, "
                + "{{radix||10||16||12}} evaluates to \"c\", and {{radix||10||16||35}} "
                + "evaluates to \"23\". Fractional numbers are not currently allowed.";
    }
    
    public String getName()
    {
        return "radix";
    }
    
}
