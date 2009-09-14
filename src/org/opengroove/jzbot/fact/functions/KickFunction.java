package org.opengroove.jzbot.fact.functions;

import org.opengroove.jzbot.JZBot;
import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.Function;

public class KickFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        context.incrementMessageCount();
        JZBot.bot.kick(context.getChannel(), arguments.get(0), arguments
                .length() > 1 ? arguments.get(1) : context.getSelf());
        return "";
    }
    
    @Override
    public String getName()
    {
        return "kick";
    }
    
    @Override
    public String getHelp(String topic)
    {
        // TODO Auto-generated method stub
        return null;
    }
    
}
