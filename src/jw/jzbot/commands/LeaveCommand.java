package jw.jzbot.commands;

import jw.jzbot.Command;
import jw.jzbot.JZBot;
import jw.jzbot.ResponseException;
import jw.jzbot.storage.Channel;

import org.jibble.pircbot.User;

/**
 * Leave instructs the bot to depart from a channel and not come back until a superop
 * requests that it come back. It can be run by superops only.
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
        JZBot.bot.sendMessage(sender, "Ok, I'll leave now. I'll remember this channel's "
                + "settings, though. Use /msg " + JZBot.bot.getNick() + " join " + channel
                + " to have me join the channel again.");
        JZBot.bot.partChannel(channel, "Leaving on request from " + sender);
    }
    
}
