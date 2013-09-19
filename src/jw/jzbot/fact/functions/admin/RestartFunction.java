package jw.jzbot.fact.functions.admin;

import jw.jzbot.JZBot;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import jw.jzbot.fact.exceptions.FactoidException;

public class RestartFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        JZBot.restart();
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {restart} -- Causes the bot to restart. The bot will exit "
            + "immediately when this is called; {restart} will be the last "
            + "function to run in the current factoid.";
    }
    
}
