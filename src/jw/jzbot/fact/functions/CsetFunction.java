package jw.jzbot.fact.functions;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;

public class CsetFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        context.getChainVars().put(arguments.get(0), arguments.get(1));
        return "";
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{cset||<varname>||<value>}} -- Sets the specified chain variable "
                + "to the specified value. Chain variables are almost exactly the same "
                + "as local variables; the only difference is that when a factoid "
                + "imports another factoid, both factoids (and any that are in turn "
                + "imported by the imported factoid) use the same set of chain variables.";
    }
    
}
