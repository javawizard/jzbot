package jw.jzbot.fact.functions.list;

import java.util.Arrays;
import java.util.Comparator;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.DelimitedSink;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

import net.sf.opengroove.common.utils.StringUtils;

public class ComparesortFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, final ArgumentList arguments, final FactContext context)
    {
        String regex = arguments.resolveString(0);
        String stringToSplit = arguments.resolveString(1);
        String delimiter = arguments.resolveString(2);
        final String prefix = arguments.resolveString(3);
        String[] tokens = stringToSplit.split(regex);
        String firstPrevious = context.getLocalVars().get(prefix + "-1");
        String secondPrevious = context.getLocalVars().get(prefix + "-2");
        Arrays.sort(tokens, new Comparator<String>()
        {
            
            @Override
            public int compare(String s1, String s2)
            {
                context.getLocalVars().put(prefix + "-1", s1);
                context.getLocalVars().put(prefix + "-2", s2);
                return Integer.parseInt(arguments.resolveString(4));
            }
        });
        if (firstPrevious != null)
            context.getLocalVars().put(prefix + "-1", firstPrevious);
        else
            context.getLocalVars().remove(prefix + "-1");
        if (secondPrevious != null)
            context.getLocalVars().put(prefix + "-2", secondPrevious);
        else
            context.getLocalVars().remove(prefix + "-2");
        DelimitedSink result = new DelimitedSink(sink, delimiter);
        for (String s : tokens)
        {
            result.next();
            result.write(s);
        }
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{comparesort||<regex>||<string>||<delimiter>||<prefix>||<comparator>}}"
                + " -- Splits <string> around the regular expression <regex>, then applies "
                + "a comparison sort to the resulting sublist. This comparison sort is "
                + "currently a modified mergesort. Pairs of items are compared by setting the "
                + "local variable <prefix>-1 to be the first value, and <prefix>-2 to be the "
                + "second value, and then evaluating <comparator>.\n"
                + "The result of this evaluation should be negative, 0, or positive if "
                + "the first item comes before, is the same as, or comes after, the second "
                + "item, respectively. Once the list is sorted, {{comparesort}} evaluates "
                + "to a <delimiter>-separated list of the sorted items.";
    }
    
}
