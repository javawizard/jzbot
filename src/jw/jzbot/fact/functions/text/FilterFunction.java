package jw.jzbot.fact.functions.text;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.DelimitedSink;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class FilterFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        String regex = arguments.resolveString(0);
        String string = arguments.resolveString(1);
        String condition = arguments.resolveString(2);
        String delimiter = "";
        if (arguments.length() > 3)
            delimiter = arguments.resolveString(3);
        String[] split = string.split(regex);
        DelimitedSink result = new DelimitedSink(sink, delimiter);
        for (String s : split)
        {
            if (!s.matches(condition))
                continue;
            result.next();
            result.write(s);
        }
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{filter||<regex>||<string>||<condition>||<delimiter>}} -- "
                + "Splits <string> around the regular expression <regex>, then reconstructs "
                + "a string made up of these strings, but delimited by <delimiter>. Only "
                + "substrings that match the regular expression <condition> will be included. "
                + "\nFor example, {{filter||,||first,second,third,fourth||^[^i]*$||-}} "
                + "would evaluate to \"second-fourth\".";
    }
    
}
