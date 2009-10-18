package org.opengroove.jzbot.commands;

import java.util.ArrayList;

import org.opengroove.jzbot.Command;
import org.opengroove.jzbot.JZBot;
import org.opengroove.jzbot.fact.FactParser;
import org.opengroove.jzbot.storage.Channel;
import org.opengroove.jzbot.storage.Factoid;
import org.opengroove.jzbot.utils.JZUtils;

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
        if (arguments.equals(""))
        {
            String s = "Opcount:" + JZBot.proxyStorage.getOpcount() + ";free,total,max:"
                    + Runtime.getRuntime().freeMemory() + ","
                    + Runtime.getRuntime().totalMemory() + ","
                    + Runtime.getRuntime().maxMemory() + ";uptime(seconds):"
                    + ((System.currentTimeMillis() - JZBot.startedAtTime) / 1000)
                    + ",functions:" + FactParser.getFunctionNames().length + ",commands:"
                    + JZBot.commands.size() + ",queue:" + JZBot.bot.getOutgoingQueueSize();
            JZBot.bot.sendMessage(pm ? sender : channel, s);
            JZBot.bot.sendMessage(pm ? sender : channel,
                    "For more info, try \"status gc\", \"status threads\", "
                            + "\"status facts\", or \"status logging\".");
        }
        else if (arguments.equals("gc"))
        {
            if (arguments.equals("gc"))
            {
                long t = System.currentTimeMillis();
                System.gc();
                JZBot.bot.sendMessage(pm ? sender : channel,
                        "Garbage-collected successfully in "
                                + (System.currentTimeMillis() - t) + " milliseconds.");
            }
        }
        else if (arguments.equals("threads"))
        {
            Thread[] threadList = new Thread[Thread.activeCount() + 20];
            int size = Thread.enumerate(threadList);
            Thread[] threads = new Thread[size];
            System.arraycopy(threadList, 0, threads, 0, size);
            ThreadGroup currentGroup = Thread.currentThread().getThreadGroup();
            String groupName = (currentGroup != null ? currentGroup.getName() : "none");
            String s = "" + size + " threads (current group is " + groupName + ")";
            ArrayList<String> strings = new ArrayList<String>();
            for (Thread thread : threads)
            {
                if (thread != null)
                {
                    strings.add("" + thread.getName() + " ("
                            + thread.getStackTrace().length + " frames, priority "
                            + thread.getPriority() + ")");
                }
            }
            if (strings.size() != 0)
            {
                String first = strings.get(0);
                strings.remove(0);
                s += (": " + first);
                strings.add(0, s);
            }
            else
            {
                strings.add(s);
            }
            JZUtils.ircSendDelimited(strings.toArray(new String[0]), ", ", JZBot.bot,
                    pm ? sender : channel);
        }
    }
    
}
