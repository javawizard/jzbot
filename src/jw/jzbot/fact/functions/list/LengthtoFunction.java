package jw.jzbot.fact.functions.list;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.utils.JZUtils;

public class LengthtoFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        String[] strings = JZUtils.delimitedLengthRestricted(arguments.get(1).split(
                arguments.get(0)), arguments.get(3), Integer.parseInt(arguments.get(2)));
        if (strings.length == 0)
            return "";
        return strings[0];
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{lengthto||<regex>||<text>||<length>||<delimiter>}} -- Splits "
                + "<text> around <regex>, then reconstrucs the resulting items into a new "
                + "string, with each item being delimited by <delimiter>. Items will "
                + "be added until the total size of the string would be more than <length>. "
                + "In this way, the resulting text will never be longer than <length>, \n"
                + "while fitting as many items as possible into the string.";
    }
    
}
