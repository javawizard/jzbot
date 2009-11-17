package jw.jzbot.fact.functions.text;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;

public class SplitindexFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        String[] tokens = arguments.get(1).split(arguments.get(0),
                Integer.parseInt(arguments.get(2)));
        try
        {
            return tokens[Integer.parseInt(arguments.get(3))];
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            return "";
        }
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{splitindex||<regex>||<text>||<max>||<index>}} -- Splits <text> "
                + "around the regular expression <regex> to a maximum length of <max>, and "
                + "then returns the <index>th element in that new list. For example, "
                + "{{splitindex||_||first_second_third_fourth||10||3}} evaluates to \"third\", "
                + "and {{splitindex||_||first_second_third_fourth||2||2}} evaluates to \""
                + "second_third_fourth\".\n"
                + "In other words, all tokens after (and including) the <max>th token are "
                + "concatenated (split by the delimiter that <regex> matched) and used as "
                + "the last token. If <index> is greater than the number of items that there"
                + " are in the list, splitindex evaluates to the empty string.";
    }
    
}
