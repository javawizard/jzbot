package jw.jzbot.fact.functions.list;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;

public class CountFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        if (arguments.get(1).equals(""))
            return "0";
        return "" + (arguments.get(1).split(arguments.get(0)).length);
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{count||<regex>||<string>}} -- Splits <string> around <regex>, "
                + "and then counts the number of items in this list. This essentially "
                + "counts how many times <regex> appears in <string>, and then returns "
                + "this number plus 1, or 0 if <string> is empty. Trailing blank items "
                + "are not counted.";
    }
    
}
