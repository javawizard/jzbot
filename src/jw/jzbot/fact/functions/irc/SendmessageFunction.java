package jw.jzbot.fact.functions.irc;

import jw.jzbot.ConnectionWrapper;
import jw.jzbot.JZBot;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import jw.jzbot.fact.exceptions.FactoidException;
import jw.jzbot.scope.ScopeManager;

public class SendmessageFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        String to = arguments.resolveString(0);
        String message = arguments.resolveString(1);
        context.incrementMessageCount();
        ScopeManager.getMessenger(context, to).sendMessage(message);
    }
    
    public String getName()
    {
        return "sendmessage";
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {sendmessage|<to>|<message>} -- Sends the specified message to "
            + "the specified recipient, which can be a channel or a nickname.";
    }
    
}
