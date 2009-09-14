package org.opengroove.jzbot.fact.functions;

import org.opengroove.jzbot.JZBot;
import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.FactoidException;
import org.opengroove.jzbot.fact.Function;

public class SendactionFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        if (!JZBot.bot.isConnected())
            throw new FactoidException(
                    "Can't send messages when the bot is disconnected");
        String to = arguments.get(0);
        String message = arguments.get(1);
        context.incrementMessageCount();
        JZBot.bot.sendAction(to, message);
        try
        {
            Thread.sleep(500);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        return "";
    }
    
    @Override
    public String getName()
    {
        return "sendaction";
    }
    
    @Override
    public String getHelp(String topic)
    {
        // TODO Auto-generated method stub
        return null;
    }
    
}
