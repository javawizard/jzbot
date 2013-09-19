package jw.jzbot.fact.functions.text;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class ReverseFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        String text = arguments.resolveString(0);
        for (int i = text.length() - 1; i >= 0; i--)
        {
            sink.write(text.charAt(i));
        }
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {reverse|<text>} -- Evaluates to <text>, but with all of "
            + "the characters reversed. For example, {reverse|hello world} "
            + "evaluates to \"dlrow olleh\".";
    }
    
}
