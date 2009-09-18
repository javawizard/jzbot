package org.opengroove.jzbot.fact.functions;

import org.opengroove.jzbot.JZBot;
import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.Function;

public class CancelFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        JZBot.futureFactoids.remove(arguments.get(0));
        return "";
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{cancel||<key>}} -- Cancels the future task that was created with "
                + "the specified key. If there is no such future task, nothing happens.";
    }
    
    @Override
    public String getName()
    {
        return "cancel";
    }
    
}
