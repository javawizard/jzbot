package jw.jzbot.fact.functions.vars;

import jw.jzbot.JZBot;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import jw.jzbot.storage.MapEntry;

public class PgetFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        MapEntry entry = JZBot.storage.getPersistentVariable(arguments.get(0));
        if (entry == null)
            return "";
        return entry.getValue();
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{pget||<varname>}} -- Same as {{get||<varname>}}, but gets the specified "
                + "persistent variable instead of the specified global variable. See \"%HELPCMD% "
                + "functions pset\" for information on the difference between persistent variables "
                + "and global variables.";
    }
    
}
