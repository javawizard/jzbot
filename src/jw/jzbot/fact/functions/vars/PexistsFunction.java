package jw.jzbot.fact.functions.vars;

import jw.jzbot.JZBot;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import jw.jzbot.storage.MapEntry;

public class PexistsFunction extends Function
{

    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        MapEntry entry = JZBot.storage.getPersistentVariable(arguments.resolveString(0));
        sink.write(entry == null ? "0" : "1");
    }

    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {pexists|<varname>} -- Returns 1 if the specified persistent variable exists, 0 if it does not.";
    }

}
