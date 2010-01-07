package jw.jzbot.fact.functions.vars;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class CsetFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        context.getChainVars().put(arguments.resolveString(0), arguments.resolveString(1));
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {cset|<varname>|<value>} -- Sets the specified chain variable "
                + "to the specified value. Chain variables are almost exactly the same "
                + "as local variables; the only difference is that when a factoid "
                + "imports another factoid, both factoids (and any that are in turn "
                + "imported by the imported factoid) use the same set of chain variables.";
    }
    
}
