package org.opengroove.jzbot.commands;

import java.util.ArrayList;

import org.opengroove.jzbot.Command;
import org.opengroove.jzbot.JZBot;
import org.opengroove.jzbot.fact.FactParser;
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
                + ((System.currentTimeMillis() - JZBot.startedAtTime) / 1000)
                + ",functions:" + FactParser.getFunctionNames().length
                + ",commands:" + JZBot.commands.size() + ",queue:"
                + JZBot.bot.getOutgoingQueueSize();
        if (arguments.equals("gc"))
        {
            long t = System.currentTimeMillis();
            System.gc();
            s += ",gctime:" + (System.currentTimeMillis() - t);
        }
        JZBot.bot.sendMessage(pm ? sender : channel, s);
    }
    
}
