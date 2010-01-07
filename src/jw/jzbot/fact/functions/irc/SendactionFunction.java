package jw.jzbot.fact.functions.irc;

import jw.jzbot.JZBot;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.FactoidException;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class SendactionFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        if (!context.checkedGetConnection().getConnection().isConnected())
            throw new FactoidException("Can't send messages when the bot is disconnected");
        String to = arguments.resolveString(0);
        String message = arguments.resolveString(1);
        context.incrementMessageCount();
        context.checkedGetConnection().getConnection().sendAction(to, message);
        try
        {
            Thread.sleep(500);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
    
    public String getName()
    {
        return "sendaction";
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {sendaction|<to>|<message>} -- Sends the specified message to "
                + "the specified recipient as if it were typed at an IRC client with \"/me\" "
                + "at the beginning.";
    }
    
}
