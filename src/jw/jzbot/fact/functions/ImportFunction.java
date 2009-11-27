package jw.jzbot.fact.functions;

import jw.jzbot.JZBot;
import jw.jzbot.JZBot.ImportLevel;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class ImportFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        context.incrementImportCount();
        // FIXME: re-work the import functionality to write to the main sink instead of
        // writing to a string sink and then copying into this sink
        sink.write(JZBot.doFactImport(context.getChannel(), arguments, context.getSender(),
                true, context.getQuota(), ImportLevel.any));
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
