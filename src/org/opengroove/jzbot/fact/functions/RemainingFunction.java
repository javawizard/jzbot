package org.opengroove.jzbot.fact.functions;

import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.Function;

public class RemainingFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{remaining||<key>}} -- Evaluates to the number of milliseconds "
                + "left before the future whose key is <key> will run, or the empty string "
                + "if there is no future with the specified key.";
    }
    
}
