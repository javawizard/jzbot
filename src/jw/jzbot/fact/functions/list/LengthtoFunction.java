package jw.jzbot.fact.functions.list;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import jw.jzbot.utils.JZUtils;

public class LengthtoFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        String[] strings = JZUtils.delimitedLengthRestricted(arguments.resolveString(1)
                .split(arguments.resolveString(0)), arguments.resolveString(3), Integer
                .parseInt(arguments.resolveString(2)));
        if (strings.length != 0)
            sink.write(strings[0]);
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
