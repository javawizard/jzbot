package org.opengroove.jzbot.fact.functions;

import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.Function;

public class PadFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        int number = Integer.parseInt(arguments.get(0));
        String c = arguments.get(1);
        String value = arguments.get(2);
        return pad(number,c,value);
    }
    
    public static String pad(int targetLength, String toPadWith, String value)
    {
        while (value.length() < targetLength)
        {
            value = toPadWith + value;
        }
        return value;
    }

    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{pad||<number>||<char>||<value>}} -- Evaluates to <value>, but "
                + "with <char> (which must be a single character) prepended until the "
                + "resulting length is at least equal to <number>. For example, "
                + "{{pad||7||0||1234}} would evaluate to \"0001234\".";
    }
    
    public String getName()
    {
        return "pad";
    }
    
}
