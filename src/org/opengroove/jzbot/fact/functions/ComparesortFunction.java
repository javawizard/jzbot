package org.opengroove.jzbot.fact.functions;

import java.util.Arrays;
import java.util.Comparator;

import net.sf.opengroove.common.utils.StringUtils;

import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.Function;

public class ComparesortFunction extends Function
{
    
    @Override
    public String evaluate(final ArgumentList arguments,
            final FactContext context)
    {
        String regex = arguments.get(0);
        String stringToSplit = arguments.get(1);
        String delimiter = arguments.get(2);
        final String prefix = arguments.get(3);
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
                return Integer.parseInt(arguments.resolve(4));
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
        return StringUtils.delimited(tokens, delimiter);
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
