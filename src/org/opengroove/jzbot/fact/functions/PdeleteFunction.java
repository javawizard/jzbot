package org.opengroove.jzbot.fact.functions;

import org.opengroove.jzbot.JZBot;
import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.Function;
import org.opengroove.jzbot.storage.MapEntry;

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
