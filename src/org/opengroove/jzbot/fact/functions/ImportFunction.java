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
        context.incrementImportCount();
        return JZBot.doFactImport(context.getChannel(), arguments, context
                .getSender(), true, context.getQuota());
    }
    
    @Override
    public String getName()
    {
        return "import";
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{import||<factoid>||<argument1>||...}} -- Imports the specified "
                + "factoid into this one. This function evaluates to whatever the factoid "
                + "indicated ends up outputting. <factoid> is the name of the factoid, and "
                + "<argument1>, <argument2>, etc. are the arguments to be passed to the factoid.";
    }
    
}
