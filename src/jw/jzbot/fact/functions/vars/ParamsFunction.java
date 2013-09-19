package jw.jzbot.fact.functions.vars;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import jw.jzbot.fact.output.DelimitedSink;

public class ParamsFunction extends Function
{
    
    @Override
    public void evaluate(Sink oldSink, ArgumentList arguments, FactContext context)
    {
        int index = 1;
        String keyvarName = arguments.resolveString(0);
        String valuevarName = arguments.resolveString(1);
        String delimiter = arguments.length() > 3 ? arguments.resolveString(3) : "";
        DelimitedSink sink = new DelimitedSink(oldSink, delimiter);
        String oldKey = context.getLocalVars().get(keyvarName);
        String oldValue = context.getLocalVars().get(valuevarName);
        for (; context.getLocalVars().containsKey("" + index); index++)
        {
            throw new UnsupportedOperationException("TODO: actually finish this");
        }
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {params|<keyvar>|<valuevar>|<action>|<delimiter>} -- "
            + "Iterates over all of the parameter variables to this factoid. "
            + "This function is essentially identical to "
            + "{vars|local|[1-9][0-9]*|<keyvar>|<valuevar>|<action>|<delimiter>}, "
            + "provided that the parameter local variables are not modified or "
            + "set by the factoid itself.";
    }
    
}
