package jw.jzbot.commands;

import jw.jzbot.Command;
import jw.jzbot.JZBot;
import jw.jzbot.ResponseException;
import jw.jzbot.storage.Factoid;

public class RestrictCommand implements Command
{
    
    @Override
    public String getName()
    {
        return "restrict";
    }
    
    @Override
    public void run(String channel, boolean pm, String sender, String hostname,
            String arguments)
    {
        if (arguments.equals(""))
            throw new ResponseException(
                    "You need to specify the name of a factoid to restrict.");
        Factoid fact = JZBot.getChannelFactoid(channel, arguments);
        if (fact != null)
        {
            JZBot.verifyOp(channel, hostname);
        }
        else
        {
            fact = JZBot.getGlobalFactoid(arguments);
            if (fact != null)
            {
                JZBot.verifySuperop(hostname);
            }
            else
            {
                throw new ResponseException("No such factoid: " + arguments);
            }
        }
        if (fact.isRestricted())
            JZBot.bot.sendMessage(pm ? sender : channel, "" + arguments
                    + " is already restricted.");
        else
        {
            fact.setRestricted(true);
            JZBot.bot.sendMessage(pm ? sender : channel, "" + arguments
                    + " is now restricted.");
        }
    }
    
}
