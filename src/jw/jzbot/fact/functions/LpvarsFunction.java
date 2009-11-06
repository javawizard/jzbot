package jw.jzbot.fact.functions;

import jw.jzbot.JZBot;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.storage.MapEntry;

public class LpvarsFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        StringBuffer b = new StringBuffer();
        for (MapEntry entry : JZBot.storage.getPersistentVariables())
        {
            String s = entry.getKey();
            if ((arguments.length() == 0) || s.matches(arguments.get(0)))
                b.append("|").append(
                        s.replace("\\", "\\\\").replace("|", "\\|"));
        }
        if (b.length() == 0)
            return "";
        return b.substring(1);
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{lpvars||<regex>}} -- Same as {{lgvars}}, but lists "
                + "persistent variables instead of global variables. See \"%HELPCMD% "
                + "functions pset\" for information on the difference between persistent variables "
                + "and global variables.";
    }
    
}
