package jw.jzbot.fact.functions.li;

import java.util.Arrays;
import java.util.Comparator;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.DelimitedSink;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import jw.jzbot.fact.functions.conditional.IfFunction;

public class SortFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        String regex = arguments.resolveString(0);
        String string = arguments.resolveString(1);
        String delimiter = "";
        if (arguments.length() > 2)
            delimiter = arguments.resolveString(2);
        String mode = "1";
        if (arguments.length() > 3)
            mode = arguments.resolveString(3);
        final boolean caseSensitive = IfFunction.findValue(mode);
        String[] split = string.split(regex);
        Arrays.sort(split, new Comparator<String>()
        {
            
            @Override
            public int compare(String first, String second)
            {
                if (caseSensitive)
                    return first.compareTo(second);
                else
                    return first.compareToIgnoreCase(second);
            }
        });
        DelimitedSink result = new DelimitedSink(sink, delimiter);
        for (String s : split)
        {
            result.next();
            result.write(s);
        }
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {sort|<regex>|<string>|<delimiter>|<case>} -- "
                + " Splits <string> around the regular expression <regex>, sorts this resulting "
                + "list of substrings (case-sensitively if <case> is 1, otherwise "
                + "case-insensitively), then reconstructs a new string by delimiting the "
                + "substrings with <delimiter>. <case> is optional, and defaults to 1 "
                + "if not present.\n"
                + "If you want a more customized sort, consider using {comparesort}.";
    }
    
}
