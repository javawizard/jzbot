package org.opengroove.jzbot.fact.functions;

import org.opengroove.jzbot.JZBot;
import org.opengroove.jzbot.JZBot.ImportLevel;
import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.Function;

public class RunFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        context.incrementImportCount();
        JZBot.doFactImport(context.getChannel(), arguments,
                context.getSender(), true, context.getQuota(), ImportLevel.any);
        return "";
    }
    
    public String getName()
    {
        return "run";
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{run||<factoid>||<argument1>||...}} -- Runs the specified factoid "
                + "without actually importing it. This function therefore evaluates "
                + "to nothing. This is pretty much only useful when the factoid in question "
                + "has useful side effects, like setting a global variable."
                + " <factoid> is the name of the factoid, and "
                + "<argument1>, <argument2>, etc. are the arguments to be passed to the factoid.";
    }
    
}
