package jw.jzbot.fact.functions.irc;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class InviteFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        context.checkedGetConnection().getConnection().sendInvite(
                arguments.resolveString(0), arguments.resolveString(1));
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {invite|<user>|<channel>} -- Sends an invitation to the "
            + "specified user to join the specified channel. Most servers require "
            + "that the bot be an operator at the specified channel in order to "
            + "send out invites.";
    }
    
}
