package org.opengroove.jzbot.commands;

import org.opengroove.jzbot.Command;
import org.opengroove.jzbot.JZBot;

public class HelpCommand implements Command
{
    
    public String getName()
    {
        return "help";
    }
    
    public void run(String channel, boolean pm, String sender, String hostname,
        String arguments)
    {
        JZBot.bot.sendMessage(pm ? sender : channel,
            "Help doesn't work yet. Pm jcp or javawizard2539 for info on jzbot.");
        JZBot.bot.sendMessage(pm ? sender : channel,
            "You can also run the \"commandlist\" command to list commands.");
        JZBot.bot.sendMessage(pm ? sender : channel,
            "For more info, see http://code.google.com/p/jwutils/wiki/WhatIsJzbot");
    }
    
}
