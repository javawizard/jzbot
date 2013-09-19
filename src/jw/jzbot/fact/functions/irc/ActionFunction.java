package jw.jzbot.fact.functions.irc;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class ActionFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        context.setAction(true);
    }
    
    public String getName()
    {
        return "action";
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {action} -- Causes the factoid to appear using \"/me\". It "
                + "essentially causes the bot to do the equivalent of prepending the "
                + "message with \"/me\" on a typical IRC client.";
    }
    
}
