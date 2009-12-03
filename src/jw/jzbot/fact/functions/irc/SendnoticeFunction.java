package jw.jzbot.fact.functions.irc;

import jw.jzbot.JZBot;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.FactoidException;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class SendnoticeFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        if (!JZBot.bot.isConnected())
            throw new FactoidException("Can't send notices when the bot is disconnected");
        String to = arguments.resolveString(0);
        String message = arguments.resolveString(1);
        context.incrementMessageCount();
        JZBot.bot.sendNotice(to, message);
    }
    
    public String getName()
    {
        return "sendnotice";
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{sendnotice||<to>||<message>}} -- Sends the specified notice message to "
                + "the specified recipient, which is a nickname.";
    }
    
}
