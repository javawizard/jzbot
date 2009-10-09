package org.opengroove.jzbot.commands;

import org.jibble.pircbot.User;
import org.opengroove.jzbot.Command;
import org.opengroove.jzbot.JZBot;
import org.opengroove.jzbot.ResponseException;
import org.opengroove.jzbot.storage.Channel;

/**
 * Leave instructs the bot to depart from a channel and not come back until a
 * superop requests that it come back. It can be run by superops only.
 * 
 * @author Alexander Boyd
 * 
 */
public class LeaveCommand implements Command
{
    
    public String getName()
    {
        return "leave";
    }
    
    public void run(String channel, boolean pm, String sender, String hostname,
            String arguments)
    {
        if (channel == null)
            throw new ResponseException("You must specify a channel.");
        try
        {
            JZBot.verifySuperop(hostname);
        }
        catch (RuntimeException e)
        {
            User user = JZBot.getUser(channel, sender);
            if (!user.isOp())
                throw e;
        }
        Channel c = JZBot.storage.getChannel(channel);
        if (c == null)
            throw new ResponseException("I'm not a member of that channel.");
        if (c.isSuspended())
            throw new ResponseException("I've already left that channel.");
        c.setSuspended(true);
        JZBot.bot.sendMessage(pm ? sender : channel,
                "Ok, I'll leave now. I'll remember this channel's "
                        + "settings, though. Use /msg " + JZBot.bot.getNick()
                        + " join " + channel
                        + " to have me join the channel again.");
        JZBot.bot.partChannel(channel, "Leaving on request from " + sender);
    }
    
}
