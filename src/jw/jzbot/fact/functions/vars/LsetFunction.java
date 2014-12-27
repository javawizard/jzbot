package jw.jzbot.fact.functions.vars;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class LsetFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        String level = "";
        if (arguments.length() == 3) {
            level = arguments.resolveString(0);
            arguments = arguments.subList(1);
        }
        context.getAncestorAtLevel(level).getLocalVars().put(arguments.resolveString(0), arguments.resolveString(1));
    }
    
    public String getName()
    {
        return "lset";
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {lset|<varname>|<value>} or {lset|<level>|<varname>|<value>} -- Sets the specified local variable "
                + "to the specified value. Local variables are those that can be read by "
                + "using percent signs. For example, after {lset|something|Hello world} "
                + "is run in a factoid, %something% could be used and would be replaced with"
                + " \"Hello world\".";
    }
    
}
