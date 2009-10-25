package org.opengroove.jzbot.fact.functions;

import org.opengroove.jzbot.JZBot;
import org.opengroove.jzbot.JZBot.ImportLevel;
import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.Function;

public class SafeimportFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        context.incrementImportCount();
        return JZBot.doFactImport(context.getChannel(), arguments, context.getSender(),
                false, context.getQuota(), ImportLevel.any);
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{safeimport||<factoid>||<arg1>||<arg2>||...}} -- Exactly the "
                + "same as {{import||<factoid>||<arg1>||<arg2>||...}}, except that this "
                + "function won't run restricted or library factoids. If a restricted or "
                + "library factoid is passed as <factoid>, {{safeimport}} will throw "
                + "an exception.";
    }
    
}
