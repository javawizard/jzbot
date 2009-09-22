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
        // TODO Auto-generated method stub
        return null;
    }
    
}
