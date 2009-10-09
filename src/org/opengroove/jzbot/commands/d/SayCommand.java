package org.opengroove.jzbot.commands.d;

import org.opengroove.jzbot.Command;
import org.opengroove.jzbot.JZBot;

public class SayCommand implements Command
{
    
    public String getName()
    {
        return "say";
    }
    
    public void run(String channel, boolean pm, String sender, String hostname,
            String arguments)
    {
        if (channel == null)
        {
            JZBot.bot.sendMessage(sender, "You must specify a channel.");
            return;
        }
        if (!JZBot.isOp(channel, hostname))
        {
            JZBot.bot.sendMessage(pm ? sender : channel,
                    "You're not an op here.");
            return;
        }
        if (arguments.equals(""))
        {
            JZBot.bot
                    .sendMessage(pm ? sender : channel,
                            "specify an argument to say. Prefix with <ACTION> to use /me.");
            return;
        }
        System.out.println("saying " + arguments + " on behalf of " + sender);
        if (arguments.startsWith("<ACTION>")
                || arguments.startsWith("<action>"))
        {
            arguments = arguments.substring("<action>".length());
            JZBot.bot.sendAction(channel, arguments);
        }
        else
        {
            JZBot.bot.sendMessage(channel, arguments);
        }
    }
    
}
