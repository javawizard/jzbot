package jw.jzbot.fact.functions.vars;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class FirstvarFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        for (int i = 1; i < arguments.length(); i++)
        {
            if (!arguments.getString(i).trim().equals(""))
            {
                context.getLocalVars().put(arguments.getString(0), arguments.getString(i));
                break;
            }
        }
    }
    
    public String getName()
    {
        return "firstvar";
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {firstvar|<varname>|<arg1>|<arg2>|...} -- "
                + "Sets the local variable <varname> to be the first argument that "
                + "comes after it whose value does not evaluate to the empty string,"
                + " ignoring whitespace and newlines.";
    }
    
}
