package org.opengroove.jzbot.fact.functions;

import org.opengroove.jzbot.JZBot;
import org.opengroove.jzbot.JZBot.ImportLevel;
import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.Function;

public class GlobalFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        context.incrementImportCount();
        return JZBot.doFactImport(context.getChannel(), arguments, context
                .getSender(), true, context.getQuota(), ImportLevel.global);
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{global||<factoid>||<arg1>||<arg2>||...}} -- Same as {{import}}, "
                + "but imports the specified global factoid only. This acts as if there "
                + "were no channel-specific factoids named <factoid>, even if one does exist.";
    }
    
}
