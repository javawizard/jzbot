package jw.jzbot.fact.functions.text;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import jw.jzbot.fact.ArgumentList.Stack;

public class SubstringFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        Stack stack = arguments.stack();
        int start = stack.nextInt(0);
        int end = stack.nextOptionalInt(Integer.MAX_VALUE, 1);
        String s = stack.nextString();
        if(start < 0)
            start += s.length();
        if(end < 0)
            end += s.length();
        if (start < 0)
            start = 0;
        if (end > s.length())
            end = s.length();
        sink.write(s.substring(start, end));
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {substring|<start>|<end>|<text>} -- Evaluates to a substring of "
            + "<text>, which starts at the index specified by <start> and ends at <end>. "
            + "If the specified indexes are out of bounds, they will be changed to be "
            + "within bounds. Indexes are 0-based, with start being inclusive and "
            + "end being exclusive. For example, {substring|3|6|0123456789} evaluates "
            + "to \"345\". <start> and <end> can be left blank, which are "
            + "equivalent to 0 and the length of <text>, respectively. "
            + "<end> can be omitted, which means the same thing. <start> "
            + "cannot be omitted due to the resulting ambiguity about "
            + "which one of <start> and <end> was specified.";
    }
    
}
