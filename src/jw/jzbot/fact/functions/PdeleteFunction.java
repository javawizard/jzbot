package jw.jzbot.fact.functions;

import jw.jzbot.JZBot;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.storage.MapEntry;

public class PdeleteFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        MapEntry entry = JZBot.storage.getPersistentVariable(arguments.get(0));
        if (entry != null)
            JZBot.storage.getPersistentVariables().remove(entry);
        return "";
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{pdelete||<varname>}} -- Same as {{delete||<varname>}}, but deletes "
                + "the specified "
                + "persistent variable instead of the specified global variable. See \"%HELPCMD% "
                + "functions pset\" for information on the difference between persistent variables "
                + "and global variables.";
    }
    
}
