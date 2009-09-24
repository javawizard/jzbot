package org.opengroove.jzbot.fact.functions;

import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.Function;

public class LengthFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        return "" + arguments.get(0).length();
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{length||<value>}} -- Evaluates to the number of characters that are "
                + "in <value>. For example, {{length||hello}} evaluates to \"5\", and "
                + "{{length||hello world}} evaluates to \"11\".";
    }
    
}
