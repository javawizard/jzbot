package jw.jzbot.fact.functions.text;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class LastcutFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        String search = arguments.resolveString(0);
        String text = arguments.resolveString(1);
        int index = text.lastIndexOf(search);
        if(index != -1)
            text = text.substring(0, index);
        sink.write(text);
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {lastcut|<search>|<text>} -- Searches for the last location " +
        		"of <search> in <text> and discards <search> and everything after " +
        		"it in <text>, then evaluates to the new text. If <search> is not " +
        		"present, <text> is returned as-is. For example, " +
        		"{lastcut|.|123.456.789} would evaluate to \"123.456\".";
    }
    
}
