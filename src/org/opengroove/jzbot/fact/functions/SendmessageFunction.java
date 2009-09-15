package org.opengroove.jzbot.fact.functions;

import org.opengroove.jzbot.JZBot;
import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.FactoidException;
import org.opengroove.jzbot.fact.Function;

public class SendmessageFunction extends Function
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
        JZBot.bot.sendMessage(to, message);
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
        return "sendmessage";
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{sendmessage||<to>||<message>}} -- Sends the specified message to "
                + "the specified recipient, which can be a channel or a nickname.";
    }
    
}
