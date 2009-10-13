package org.opengroove.jzbot.fact.functions;

import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.Function;

public class SplitindexFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        String[] tokens = arguments.get(1).split(arguments.get(0),
                Integer.parseInt(arguments.get(2)));
        return tokens[Integer.parseInt(arguments.get(3))];
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
                + "the last token.";
    }
    
}
