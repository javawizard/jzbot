package org.opengroove.jzbot.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import net.sf.opengroove.common.utils.DataUtils;

import org.opengroove.jzbot.Command;
import org.opengroove.jzbot.ConfigVars;
import org.opengroove.jzbot.JZBot;
import org.opengroove.jzbot.fact.FactParser;
import org.opengroove.jzbot.storage.Channel;
import org.opengroove.jzbot.storage.Factoid;
import org.opengroove.jzbot.utils.JZUtils;
import org.opengroove.jzbot.utils.Pastebin;
import org.opengroove.jzbot.utils.Pastebin.Duration;

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
        if (ConfigVars.openstatus.get().equals("0"))
            JZBot.verifySuperop(hostname);
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
                            + "\"status facts\". \"status storage\", "
                            + "\"status mx\", \"status stack\", "
                            + "or \"status logging\".");
        }
        else if (arguments.equals("gc"))
        {
            long t = System.currentTimeMillis();
            System.gc();
            JZBot.bot.sendMessage(pm ? sender : channel,
                    "Garbage-collected successfully in " + (System.currentTimeMillis() - t)
                            + " milliseconds.");
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
        else if (arguments.equals("logging"))
        {
            File logsFolder = new File("storage/logs");
            long total = 0;
            ArrayList<String> strings = new ArrayList<String>();
            for (File f : logsFolder.listFiles())
            {
                if (!f.isFile())
                    continue;
                total += f.length();
                strings.add("" + f.getName() + ":" + f.length());
            }
            JZUtils.ircSendDelimited("Total log size in bytes: " + total
                    + (strings.size() > 0 ? ", per-channel: " : ""), strings
                    .toArray(new String[0]), ", ", JZBot.bot, pm ? sender : channel);
        }
        else if (arguments.equals("facts"))
        {
            int totalFacts = 0;
            String highestChannel = null;
            int highestChannelFacts = -1;
            int globalFacts = JZBot.storage.getFactoids().size();
            totalFacts += globalFacts;
            for (Channel c : JZBot.storage.getChannels().isolate())
            {
                int factCount = c.getFactoids().size();
                totalFacts += factCount;
                if (factCount > highestChannelFacts)
                {
                    highestChannelFacts = factCount;
                    highestChannel = c.getName();
                }
            }
            JZBot.bot.sendMessage(pm ? sender : channel, "Total factoids: " + totalFacts
                    + ", global factoids: " + globalFacts + ", " + highestChannel
                    + " has the most factoids (" + highestChannelFacts + " factoids)");
        }
        else if (arguments.equals("stack"))
        {
            StringBuffer b = new StringBuffer();
            Map<Thread, StackTraceElement[]> stacks = Thread.getAllStackTraces();
            for (Map.Entry<Thread, StackTraceElement[]> e : stacks.entrySet())
            {
                b.append("Thread: " + e.getKey().getName() + "\n");
                for (StackTraceElement element : e.getValue())
                {
                    b.append("    at " + element.toString() + "\n");
                }
                b.append("\n");
            }
            JZBot.bot.sendMessage(pm ? sender : channel,
                    "Stack traces of all live threads: http://pastebin.com/"
                            + Pastebin
                                    .createPost("jzbot", b.toString(), Duration.DAY, null));
        }
        else if (arguments.equals("storage"))
        {
            long totalStorageSize = DataUtils.recursiveSizeScan(new File("storage"));
            long databaseSize = new File("storage/db.data.db").length();
            long logsFolderSize = DataUtils.recursiveSizeScan(new File("storage/logs"));
            long resourcesSize = DataUtils.recursiveSizeScan(new File("resources"));
            long entireSize = DataUtils.recursiveSizeScan(new File("."));
            JZBot.bot.sendMessage(pm ? sender : channel, "Overall storage size (bytes): "
                    + totalStorageSize + ", database size: " + databaseSize
                    + ", logs folder size: " + logsFolderSize + ", resources size: "
                    + resourcesSize + ", entire installation size: " + entireSize);
        }
        else
        {
            JZBot.bot.sendMessage(pm ? sender : channel, "Invalid status command.");
        }
    }
}
