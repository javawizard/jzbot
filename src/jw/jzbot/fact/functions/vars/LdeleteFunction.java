package jw.jzbot.fact.functions.vars;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class LdeleteFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        String level = "";
        if (arguments.length() == 2) {
            level = arguments.resolveString(0);
            arguments = arguments.subList(1);
        }
        context.getAncestorAtLevel(level).getLocalVars().remove(arguments.resolveString(0));
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {ldelete|<varname>} or {ldelete|<level>|<varname} -- Deletes the local variable with "
                + "the specified name.";
    }
    
}
