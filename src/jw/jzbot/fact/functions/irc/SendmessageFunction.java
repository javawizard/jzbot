package jw.jzbot.fact.functions.irc;

import jw.jzbot.JZBot;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.FactoidException;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class SendmessageFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
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
