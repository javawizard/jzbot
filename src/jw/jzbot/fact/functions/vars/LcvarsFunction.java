package jw.jzbot.fact.functions.vars;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class LcvarsFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        // TODO: change this to use a DelimitedSink
        StringBuffer b = new StringBuffer();
        for (String s : context.getChainVars().keySet())
        {
            if ((arguments.length() == 0) || s.matches(arguments.getString(0)))
                b.append("|").append(s.replace("\\", "\\\\").replace("|", "\\|"));
        }
        if (b.length() != 0)
            sink.write(b.substring(1));
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{lcvars||<regex>}} -- Same as {{lgvars}} but for chain variables "
                + "instead of global variables.";
    }
    
    public String getName()
    {
        return "llvars";
    }
    
}
