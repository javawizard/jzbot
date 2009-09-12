package org.opengroove.jzbot.commands;

import org.opengroove.jzbot.Command;
import org.opengroove.jzbot.JZBot;
import org.opengroove.jzbot.ResponseException;
import org.opengroove.jzbot.storage.Channel;

public class JoinMessageCommand implements Command
{
    
    public String getName()
    {
        return "joinmessage";
    }
    
    public void run(String channel, boolean pm, String sender, String hostname,
        String arguments)
    {
        arguments = arguments.trim();
        JZBot.bot.verifyOp(channel, hostname);
        String[] argumentTokens = arguments.split(" ", 2);
        Channel c = JZBot.storage.getChannel(channel);
        if (arguments.equals(""))
        {
            if (c.getJoinFactoid() != null)
            {
                JZBot.bot.sendMessage(pm ? sender : channel,
                    "The current join message is " + c.getJoinFactoid() + ".");
                JZBot.bot.sendMessage(pm ? sender : channel,
                    "Use joinmessage set to set a new one, or "
                        + "joinmessage delete to delete this one.");
            }
            else
            {
                JZBot.bot.sendMessage(pm ? sender : channel,
                    "There is no join message on this channel. use "
                        + "joinmessage set to create one.");
            }
            return;
        }
        else if (argumentTokens[0].equals("set"))
        {
            if (argumentTokens.length == 1)
                throw new ResponseException(
                    "do joinmessage set FACTNAME FACTNAME2 ... to set a join message.");
            c.setJoinFactoid(argumentTokens[1]);
            JZBot.bot.sendMessage(pm ? sender : channel, "Join message set.");
        }
        else if (argumentTokens[0].equals("delete"))
        {
            c.setJoinFactoid(null);
            JZBot.bot.sendMessage(pm ? sender : channel, "Join message deleted.");
        }
    }
}
