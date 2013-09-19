package jw.jzbot.fact.functions;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class FlipFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        throw new UnsupportedOperationException(
                "The flip function hasn't yet been written.");
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {flip|<text>} -- Flips the specified text upside-down. This "
                + "involves reversing the text and substituting each letter for an "
                + "equivalent character that is flipped upside-down. This only works "
                + "correctly if UTF-8 is used as the charset.";
    }
    
}
