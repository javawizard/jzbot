package org.opengroove.jzbot.commands;

import java.util.ArrayList;

import org.opengroove.jzbot.Command;
import org.opengroove.jzbot.JZBot;
import org.opengroove.jzbot.storage.Channel;
import org.opengroove.jzbot.storage.Factoid;

public class StatusCommand implements Command
{
    
    @Override
    public String getName()
    {
        return "status";
    }
    
    @Override
    public void run(String channel, boolean pm, String sender, String hostname,
            String arguments)
    {
        String s = "Opcount:" + JZBot.proxyStorage.getOpcount()
                + ";free,total,max:" + Runtime.getRuntime().freeMemory() + ","
                + Runtime.getRuntime().totalMemory() + ","
                + Runtime.getRuntime().maxMemory() + ";uptime(seconds):"
                + ((System.currentTimeMillis() - JZBot.startedAtTime) / 1000);
        JZBot.bot.sendMessage(pm ? sender : channel, s);
    }
    
}
