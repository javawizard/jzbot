package org.opengroove.jzbot.fact.functions;

import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.Function;

public class LgvarsFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        StringBuffer b = new StringBuffer();
        for (String s : context.getGlobalVars().keySet())
        {
            b.append("|").append(s.replace("\\", "\\\\").replace("|", "\\|"));
        }
        if (b.length() == 0)
            return "";
        return b.substring(1);
    }
    
    @Override
    public String getName()
    {
        return "lgvars";
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{lgvars}} -- Returns a pipe-delimited list of global variable"
                + " names, with pipes in those names escaped (pipes in variable names "
                + "are still a bad idea anyway) with backslashes. This is mostly for "
                + "when you're trying to debug stuff and you want to see the list of "
                + "global variables that exist.";
    }
    
}
