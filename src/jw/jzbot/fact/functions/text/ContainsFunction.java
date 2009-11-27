package jw.jzbot.fact.functions.text;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class ContainsFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        if (arguments.resolveString(1).contains(arguments.resolveString(0)))
            sink.write('1');
        else
            sink.write('0');
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{contains||<substring>||<string>}} -- Evaluates to 1 if <string> "
                + "contains <substring> anywhere in it, or 0 if it doesn't.";
    }
    
}
