package org.opengroove.jzbot.fact.functions;

import org.opengroove.jzbot.JZBot;
import org.opengroove.jzbot.JZBot.ImportLevel;
import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.Function;

public class ExactFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        context.incrementImportCount();
        return JZBot.doFactImport(context.getChannel(), arguments, context
                .getSender(), true, context.getQuota(), ImportLevel.exact);
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{exact||<factoid>||<arg1>||<arg2>||...}} -- Same as {{import}}, "
                + "but imports the factoid at exactly the same scope as this factoid. If "
                + "this is a global factoid, then this will end up calling the specified "
                + "global factoid even if an equivalent, channel-specific factoid exists.";
    }
    
}
