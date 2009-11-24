package jw.jzbot.fact.functions.text;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class AfterpadFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
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
            value = value + toPadWith;
        }
        return value;
    }

    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{afterpad||<number>||<char>||<value>}} -- Evaluates to <value>, but "
                + "with <char> (which must be a single character) appended until the "
                + "resulting length is at least equal to <number>. For example, "
                + "{{pad||7||0||1234}} would evaluate to \"1234000\".";
    }
    
    public String getName()
    {
        return "pad";
    }
    
}
