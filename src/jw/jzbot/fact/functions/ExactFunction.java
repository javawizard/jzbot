package jw.jzbot.fact.functions;

import jw.jzbot.JZBot;
import jw.jzbot.JZBot.ImportLevel;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class ExactFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
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
