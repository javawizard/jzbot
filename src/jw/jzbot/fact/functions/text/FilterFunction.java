package jw.jzbot.fact.functions.text;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class FilterFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        String regex = arguments.get(0);
        String string = arguments.get(1);
        String condition = arguments.get(2);
        String delimiter = "";
        if (arguments.length() > 3)
            delimiter = arguments.get(3);
        String[] split = string.split(regex);
        StringBuffer result = new StringBuffer();
        boolean first = true;
        for (String s : split)
        {
            if (!s.matches(condition))
                continue;
            if (first)
                first = false;
            else
                result.append(delimiter);
            result.append(s);
        }
        return result.toString();
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
