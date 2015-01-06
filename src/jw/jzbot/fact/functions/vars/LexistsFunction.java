package jw.jzbot.fact.functions.vars;

import jw.jzbot.JZBot;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import jw.jzbot.storage.MapEntry;

public class LexistsFunction extends Function
{

    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        String level = "";
        if (arguments.length() == 2) {
            level = arguments.resolveString(0);
            arguments = arguments.subList(1);
        }
        String var = context.getAncestorAtLevel(level).getLocalVars().get(arguments.resolveString(0));
        sink.write(var == null ? "0" : "1");
    }

    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {lexists|<varname>} or {lexists|<level>|<varname>} -- Returns 1 if the specified local " +
                "variable exists, 0 if it does not.";
    }

}
