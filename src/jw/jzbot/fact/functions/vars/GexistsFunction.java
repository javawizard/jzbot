package jw.jzbot.fact.functions.vars;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class GexistsFunction extends Function
{

    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        String var = context.getGlobalVars().get(arguments.resolveString(0));
        sink.write(var == null ? "0" : "1");
    }

    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {gexists|<varname>} -- Returns 1 if the specified global " +
                "variable exists, 0 if it does not.";
    }

}
