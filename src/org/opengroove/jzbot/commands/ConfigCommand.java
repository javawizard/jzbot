package org.opengroove.jzbot.commands;

import org.opengroove.jzbot.Command;
import org.opengroove.jzbot.JZBot;

public class ConfigCommand implements Command
{
    
    public String getName()
    {
        return "config";
    }
    
    public void run(String channel, boolean pm, String sender, String hostname,
        String arguments)
    {
        if (!JZBot.isSuperop(hostname))
        {
            JZBot.bot.sendMessage(pm ? sender : channel, "You're not a superop.");
            return;
        }
        String[] tokens = arguments.split(" ");
        if (tokens[0].equals("delay"))
        {
            if (tokens.length == 1)
            {
                JZBot.bot.sendMessage(pm ? sender : channel, "Delay is "
                    + JZBot.bot.getMessageDelay());
                return;
            }
            int delay = Integer.parseInt(tokens[1]);
            JZBot.bot.setMessageDelay(delay);
            JZBot.bot.sendMessage(pm ? sender : channel, "Delay set to " + delay
                + " (session local)");
        }
        else
        {
            
        }
    }
}
