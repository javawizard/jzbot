package org.opengroove.jzbot.fact.functions;

import org.opengroove.jzbot.JZBot;
import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.Function;

public class ImportFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        return JZBot.doFactImport(context.getChannel(),
                arguments, context.getSender());
    }
    
    @Override
    public String getName()
    {
        return "import";
    }
    
}
