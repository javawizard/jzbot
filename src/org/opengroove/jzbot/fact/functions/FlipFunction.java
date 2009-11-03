package org.opengroove.jzbot.fact.functions;

import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.Function;

public class FlipFunction extends Function
{
   
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{flip||<text>}} -- Flips the specified text upside-down. This "
                + "involves reversing the text and substituting each letter for an "
                + "equivalent character that is flipped upside-down.";
    }
    
}
