package jw.jzbot.fact.functions;

import jw.jzbot.JZBot;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;

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
    
    public String getName()
    {
        return "cancel";
    }
    
}
