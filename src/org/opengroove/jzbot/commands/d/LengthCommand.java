package org.opengroove.jzbot.commands.d;

import org.opengroove.jzbot.Command;
import org.opengroove.jzbot.JZBot;

public class LengthCommand implements Command
{
    
    @Override
    public String getName()
    {
        return "length";
    }
    
    @Override
    public void run(String channel, boolean pm, String sender, String hostname,
            String arguments)
    {
        JZBot.bot.sendMessage(pm ? sender : channel, "" + arguments.length());
    }
}
