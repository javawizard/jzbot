package jw.jzbot.fact.functions.text;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class SplitindexFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        // TODO: consider changing this into a looped iteration with a Matcher, as this
        // would only parse until the indexed item, not further into the list of items,
        // therefore running faster. Maybe run a benchmark that tests the speed of the two
        // methods to see which one is faster.
        int max;
        int index;
        if (arguments.length() > 3)
        {
            max = Integer.parseInt(arguments.resolveString(2));
            index = Integer.parseInt(arguments.resolveString(3));
        }
        else
        {
            max = 1000 * 1000;
            index = Integer.parseInt(arguments.resolveString(2));
        }
        String[] tokens = arguments.resolveString(1).split(arguments.resolveString(0), max);
        try
        {
            sink.write(tokens[index]);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {splitindex|<regex>|<text>|<max>|<index>} -- Splits <text> "
            + "around the regular expression <regex> to a maximum length of <max>, and "
            + "then returns the <index>th element in that new list. For example, "
            + "{splitindex|_|first_second_third_fourth|10|3} evaluates to \"third\", "
            + "and {splitindex|_|first_second_third_fourth|2|2} evaluates to \""
            + "second_third_fourth\".\n"
            + "In other words, all tokens after (and including) the <max>th token are "
            + "concatenated (split by the delimiter that <regex> matched) and used as "
            + "the last token. If <index> is greater than the number of items that there"
            + " are in the list, splitindex evaluates to the empty string. <max> "
            + "is optional.";
    }
    
}
