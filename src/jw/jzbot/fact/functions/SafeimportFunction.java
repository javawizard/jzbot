package jw.jzbot.fact.functions;

import jw.jzbot.JZBot;
import jw.jzbot.JZBot.ImportLevel;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class SafeimportFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        context.incrementImportCount();
        // FIXME: re-work the import functionality to write to the main sink instead of
        // writing to a string sink and then copying into this sink
        sink.write(JZBot.doFactImport(context.getServer(), context.getChannel(), arguments,
                context.getSender(), context.getSource(), false, context.getQuota(),
                ImportLevel.any));
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
