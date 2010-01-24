package jw.jzbot.commands;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Map;

import javax.management.ObjectName;

import jw.jzbot.Command;
import jw.jzbot.ConfigVars;
import jw.jzbot.JZBot;
import jw.jzbot.Messenger;
import jw.jzbot.ResponseException;
import jw.jzbot.ServerUser;
import jw.jzbot.fact.FactParser;
import jw.jzbot.pastebin.PastebinService;
import jw.jzbot.storage.Channel;
import jw.jzbot.storage.Factoid;
import jw.jzbot.utils.JZUtils;
import jw.jzbot.utils.Pastebin;
import jw.jzbot.utils.Pastebin.Duration;

import net.sf.opengroove.common.utils.DataUtils;
import net.sf.opengroove.common.utils.StringUtils;

public class StatusCommand implements Command
{
    
    @Override
    public String getName()
    {
        return "status";
    }
    
    @Override
    public void run(String server, String channel, boolean pm, ServerUser sender,
            Messenger source, String arguments)
    {
        if (ConfigVars.openstatus.get().equals("0"))
            sender.verifySuperop();
        if (arguments.equals(""))
        {
            String s =
                    "Opcount:" + JZBot.proxyStorage.getOpcount() + ";free,total,max:"
                        + format(Runtime.getRuntime().freeMemory()) + ","
                        + format(Runtime.getRuntime().totalMemory()) + ","
                        + format(Runtime.getRuntime().maxMemory()) + ";uptime(seconds):"
                        + ((System.currentTimeMillis() - JZBot.startedAtTime) / 1000)
                        + ",functions:" + FactParser.getFunctionNames().length
                        + ",commands:" + JZBot.commands.size() + ",queue:"
                        + JZBot.getServer(server).getConnection().getOutgoingQueueSize()
                        + ",pastebins:" + PastebinService.getProviderCount();
            source.sendMessage(s);
            source.sendMessage("For more info, try \"status gc\", \"status threads\", "
                + "\"status facts\". \"status storage\", "
                + "\"status mx\", \"status stack\", "
                + "\"status os\", \"status logging\", " + "or \"status hostname\".");
        }
        else if (arguments.equals("gc"))
        {
            long t = System.currentTimeMillis();
            System.gc();
            source.sendMessage("Garbage-collected successfully in "
                + (System.currentTimeMillis() - t) + " milliseconds.");
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
            if (strings.size() > 10)
            {
                String pastebin =
                        JZBot.pastebinNotice(StringUtils.delimited(strings
                                .toArray(new String[0]), "\n"), null);
                source.sendMessage("" + size + " threads: " + pastebin);
            }
            else
            {
                JZUtils.ircSendDelimited(strings.toArray(new String[0]), ", ", source);
            }
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
                strings.add("" + f.getName() + ":" + format(f.length()));
            }
            JZUtils.ircSendDelimited("Total log size in bytes: " + format(total)
                + (strings.size() > 0 ? ", per-channel: " : ""), strings
                    .toArray(new String[0]), ", ", source);
        }
        else if (arguments.equals("facts"))
        {
            // int totalFacts = 0;
            // String highestChannel = null;
            // int highestChannelFacts = -1;
            // int globalFacts = JZBot.storage.getFactoids().size();
            // totalFacts += globalFacts;
            // for (Channel c : JZBot.storage.getChannels().isolate())
            // {
            // int factCount = c.getFactoids().size();
            // totalFacts += factCount;
            // if (factCount > highestChannelFacts)
            // {
            // highestChannelFacts = factCount;
            // highestChannel = c.getName();
            // }
            // }
            // JZBot.getServer(server).sendMessage(
            // pm ? sender : channel,
            // "Total factoids: " + totalFacts + ", global factoids: " + globalFacts
            // + ", " + highestChannel + " has the most factoids ("
            // + highestChannelFacts + " factoids)");
            // TODO: make this work. Show overall stats, and then the server with the
            // highest amount and the channel with the highest amount. Maybe also state
            // which server is highest for server-level facts and which server is highest
            // overall with channel facts included, and sums both with and without
            // server-level facts.
            throw new ResponseException(
                    "\"~status facts\" needs to be re-written since the "
                        + "change to multiple servers. Until this is done, "
                        + "\"~status facts\" does not work.");
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
            source.sendMessage("Stack traces of all live threads: "
                + Pastebin.createPost("jzbot", b.toString(), Duration.DAY, null, null));
        }
        else if (arguments.equals("storage"))
        {
            long totalStorageSize = DataUtils.recursiveSizeScan(new File("storage"));
            long databaseSize = new File("storage/db.data.db").length();
            long logsFolderSize = DataUtils.recursiveSizeScan(new File("storage/logs"));
            long resourcesSize = DataUtils.recursiveSizeScan(new File("resources"));
            long entireSize = DataUtils.recursiveSizeScan(new File("."));
            source.sendMessage("Overall storage size: " + format(totalStorageSize)
                + ", database size: " + format(databaseSize) + ", logs folder size: "
                + format(logsFolderSize) + ", resources size: " + format(resourcesSize)
                + ", entire installation size: " + format(entireSize));
        }
        else if (arguments.equals("os"))
        {
            source.sendMessage("memory: "
                + format(getOsAttribute("TotalPhysicalMemorySize")) + ", free: "
                + format(getOsAttribute("FreePhysicalMemorySize")) + ", swap: "
                + format(getOsAttribute("TotalSwapSpaceSize")) + ", free: "
                + format(getOsAttribute("FreeSwapSpaceSize")) + ", open FDs: "
                + getOsAttribute("OpenFileDescriptorCount") + ", max FDs: "
                + getOsAttribute("MaxFileDescriptorCount") + ", load average: "
                + getOsAttribute("SystemLoadAverage") + ", bot CPU time: "
                + getOsAttribute("ProcessCpuTime"));
        }
        else if (arguments.equals("hostname"))
        {
            source.sendMessage("Your hostname is " + sender.getHostname());
        }
        else
        {
            source.sendMessage("Invalid status command.");
        }
    }
    
    public static String getAttribute(String object, String attribute)
    {
        try
        {
            return ""
                + ManagementFactory.getPlatformMBeanServer().getAttribute(
                        new ObjectName(object), attribute);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return "?";
        }
    }
    
    public static String format(long v)
    {
        return JZBot.datasize(v);
    }
    
    public static String format(String c)
    {
        try
        {
            return format(Long.parseLong(c));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return "??";
        }
    }
    
    public static String getOsAttribute(String attribute)
    {
        return getAttribute("java.lang:type=OperatingSystem", attribute);
    }
}
