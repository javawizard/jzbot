package jw.jzbot.fact.functions.collections;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class CountFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        if (arguments.getString(1).equals(""))
            sink.write('0');
        else
            sink.write(arguments.getString(1).split(arguments.getString(0)).length);
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {count|<regex>|<string>} -- Splits <string> around <regex>, "
                + "and then counts the number of items in this list. This essentially "
                + "counts how many times <regex> appears in <string>, and then returns "
                + "this number plus 1, or 0 if <string> is empty. Trailing blank items "
                + "are not counted.";
    }
    
}
