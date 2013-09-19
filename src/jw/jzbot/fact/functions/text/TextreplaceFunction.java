package jw.jzbot.fact.functions.text;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class TextreplaceFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        sink.write(arguments.resolveString(0).replace(arguments.resolveString(1),
                arguments.resolveString(2)));
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {textreplace|<text>|<search>|<replacement>} -- Finds "
            + "all occurrences of <search> occurring within <text> and replaces "
            + "them with <replacement>.";
    }
    
}
