package jw.jzbot.fact.functions.vars;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class LlvarsFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        StringBuffer b = new StringBuffer();
        String delimiter = "|";
        if (arguments.length() == 2)
            delimiter = arguments.resolveString(1);
        for (String s : context.getLocalVars().keySet())
        {
            if ((arguments.length() == 0) || s.matches(arguments.getString(0)))
                b.append(delimiter).append(s);
        }
        if (b.length() != 0)
            sink.write(b.substring(1));
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {llvars|<regex>|<delimiter>} -- Same as {lgvars} but for local variables "
            + "instead of global variables.";
    }
    
    public String getName()
    {
        return "llvars";
    }
    
}
