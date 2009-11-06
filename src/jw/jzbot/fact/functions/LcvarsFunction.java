package jw.jzbot.fact.functions;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;

public class LcvarsFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        StringBuffer b = new StringBuffer();
        for (String s : context.getChainVars().keySet())
        {
            if ((arguments.length() == 0) || s.matches(arguments.get(0)))
            b.append("|").append(s.replace("\\", "\\\\").replace("|", "\\|"));
        }
        if (b.length() == 0)
            return "";
        return b.substring(1);
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
