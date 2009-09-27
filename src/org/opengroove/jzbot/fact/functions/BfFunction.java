package org.opengroove.jzbot.fact.functions;

import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.Function;

public class BfFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{bf||<code>||<size>}} -- Executes <code> as BF code and evaluates "
                + "to whatever the code outputs. The memory bank provided to the code is "
                + "<size> positions. <size> is optional, and will be 1024 if not present. "
                + "Each position is a 32-bit signed integer.\n" +
                		"For more information on the BF programming language, see ";
    }
    
}
