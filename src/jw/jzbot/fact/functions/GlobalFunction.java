package jw.jzbot.fact.functions;

import jw.jzbot.JZBot;
import jw.jzbot.JZBot.ImportLevel;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class GlobalFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        context.incrementImportCount();
        // FIXME: re-work the import functionality to write to the main sink instead of
        // writing to a string sink and then copying into this sink
        sink.write(JZBot.doFactImport(context.getChannel(), arguments, context.getSender(),
                true, context.getQuota(), ImportLevel.global));
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{global||<factoid>||<arg1>||<arg2>||...}} -- Same as {{import}}, "
                + "but imports the specified global factoid only. This acts as if there "
                + "were no channel-specific factoids named <factoid>, even if one does exist.";
    }
    
}
