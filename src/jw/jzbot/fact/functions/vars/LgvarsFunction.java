package jw.jzbot.fact.functions.vars;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class LgvarsFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        StringBuffer b = new StringBuffer();
        for (String s : context.getGlobalVars().keySet())
        {
            if ((arguments.length() == 0) || s.matches(arguments.get(0)))
                b.append("|").append(
                        s.replace("\\", "\\\\").replace("|", "\\|"));
        }
        if (b.length() == 0)
            return "";
        return b.substring(1);
    }
    
    public String getName()
    {
        return "lgvars";
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{lgvars||<regex>}} -- Returns a pipe-delimited list of global variable"
                + " names, with pipes in those names escaped (pipes in variable names "
                + "are still a bad idea anyway) with backslashes. This is mostly for "
                + "when you're trying to debug stuff and you want to see the list of "
                + "global variables that exist.\n"
                + "<regex> is optional, but if it's present, only the names of variables "
                + "that match <regex> will be returned.";
    }
    
}
