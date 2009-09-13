package org.opengroove.jzbot.fact.functions;

import org.opengroove.jzbot.JZBot;
import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.Function;

public class SendmessageFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        String to = arguments.get(0);
        String message = arguments.get(1);
        JZBot.bot.sendMessage(to, message);
        return "";
    }
    
    @Override
    public String getName()
    {
        return "sendmessage";
    }
    
}
