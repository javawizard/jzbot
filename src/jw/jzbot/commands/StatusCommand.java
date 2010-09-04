package jw.jzbot.commands;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import javax.management.ObjectName;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

import jw.jzbot.Command;
import jw.jzbot.JZBot;
import jw.jzbot.ResponseException;
import jw.jzbot.configuration.Configuration;
import jw.jzbot.fact.FactParser;
import jw.jzbot.pastebin.PastebinService;
import jw.jzbot.scope.Messenger;
import jw.jzbot.scope.UserMessenger;
import jw.jzbot.utils.Utils;
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
    public void run(String server, String channel, boolean pm, UserMessenger sender,
            Messenger source, String arguments)
    {
        // TODO: re-enable this. Also consider enabling one to just disable
        // processing-intensive functions like "~status version".
        // if (ConfigVars.openstatus.get().equals("0"))
        // sender.verifySuperop();
        if (arguments.equals(""))
        {
            String s =
                    "Opcount:" + JZBot.proxyStorage.getOpcount() + "; free,total,max:"
                        + format(Runtime.getRuntime().freeMemory()) + ","
                        + format(Runtime.getRuntime().totalMemory()) + ","
                        + format(Runtime.getRuntime().maxMemory()) + "; uptime(seconds):"
                        + ((System.currentTimeMillis() - JZBot.startedAtTime) / 1000)
                        + ",functions:" + FactParser.getFunctionNames().length
                        + ",commands:" + JZBot.commands.size() + ",queue:"
                        + JZBot.getServer(server).getConnection().getOutgoingQueueSize()
                        + ",pastebins:" + PastebinService.getProviderCount();
            source.sendSpaced(s);
            source
                    .sendSpaced("For more info, try \"status gc\", \"status threads\", "
                        + "\"status facts\". \"status storage\", "
                        + "\"status mx\", \"status stack\", "
                        + "\"status os\", \"status logging\", "
                        + "\"status version\", \"status hostname\", "
                        + "or \"status proxytrace\".");
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
                Utils.ircSendDelimited(strings.toArray(new String[0]), ", ", source);
            }
        }
        else if (arguments.equals("logging"))
        {
            // File logsFolder = new File("storage/logs");
            // long total = 0;
            // ArrayList<String> strings = new ArrayList<String>();
            // for (File f : logsFolder.listFiles())
            // {
            // if (!f.isFile())
            // continue;
            // total += f.length();
            // strings.add("" + f.getName() + ":" + format(f.length()));
            // }
            // JZUtils.ircSendDelimited("Total log size in bytes: " + format(total)
            // + (strings.size() > 0 ? ", per-channel: " : ""), strings
            // .toArray(new String[0]), ", ", source);
            throw new ResponseException("This is disabled due to not "
                + "pastebinning on large output values. jcp willF fix this "
                + "at some point; not sure when yet.");
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
            long relationalStoreSize = new File("storage/relational/rs.data.db").length();
            long logsFolderSize = DataUtils.recursiveSizeScan(new File("storage/logs"));
            long resourcesSize = DataUtils.recursiveSizeScan(new File("resources"));
            long entireSize = DataUtils.recursiveSizeScan(new File("."));
            source.sendMessage("Overall storage size:" + format(totalStorageSize)
                + ";database size:" + format(databaseSize) + ";relational store size:"
                + relationalStoreSize + ";logs folder size;" + format(logsFolderSize)
                + ";resources size:" + format(resourcesSize) + ";entire installation size;"
                + format(entireSize));
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
        else if (arguments.equals("version"))
        {
            if (!sender.isSuperop())
                throw new ResponseException("Due to the relatively high "
                    + "level of computational power needed to figure out "
                    + "the current version, only superops are allowed to "
                    + "run this command at present.");
            sendVersion(source);
        }
        else if (arguments.equals("hostname"))
        {
            source.sendMessage("Your hostname is " + sender.getHostname());
        }
        else if (arguments.equals("proxytrace"))
        {
            String initial = "";
            boolean tracingEnabled = Configuration.getBool(null, "proxytrace");
            Map<String, Long> map = JZBot.proxyStorage.getCurrentTracingInfo();
            if (tracingEnabled)
                initial +=
                        "ProxyStorage tracing is currently enabled. "
                            + "Current tracing information: ";
            else
            {
                initial +=
                        "ProxyStorage tracing is currently disabled. "
                            + "You can enable it with \"config proxytrace 1\". ";
                if (map.size() > 0)
                    initial +=
                            "Tracing information from the last time tracing "
                                + "was enabled: ";
            }
            String pastebinText =
                    "Here's the ProxyStorage tracing "
                        + "information. Each line consists of a number "
                        + "followed by an SQL statement. Parameters are "
                        + "represented by question marks. The number "
                        + "represents the number of times that the "
                        + "statement in question was run since tracing was enabled.\n\n\n";
            for (Map.Entry<String, Long> entry : map.entrySet())
                pastebinText += entry.getValue() + "  " + entry.getKey() + "\n";
            initial += JZBot.pastebinNotice(pastebinText, null);
            source.sendSpaced(initial);
        }
        else
        {
            source.sendMessage("Invalid status command.");
        }
    }
    
    private void sendVersion(Messenger source)
    {
        try
        {
            /*
             * First, we figure out our local revision.
             */
            Process p =
                    Runtime.getRuntime().exec(getSvnExecutable() + " info --xml -R .",
                            null, new File("."));
            Utils.sinkStream(p.getErrorStream());
            Document doc = new SAXBuilder().build(p.getInputStream());
            int exitCode = p.waitFor();
            if (exitCode != 0)
                throw new RuntimeException("Exit code from svn process was " + exitCode
                    + "; expected 0");
            List list =
                    XPath.selectNodes(doc.getRootElement(), "/info/entry/commit/@revision");
            long latestLocalRevision = 0;
            for (Object o : list)
            {
                if (o instanceof Attribute)
                {
                    Attribute att = (Attribute) o;
                    long revision = Long.parseLong(att.getValue());
                    if (revision > latestLocalRevision)
                        latestLocalRevision = revision;
                }
                else
                {
                    System.out.println("WARNING: XPath element was not an attribute: " + o);
                }
            }
            /*
             * Then we figure out the current revision that the repository's at, which
             * will tell us if we're out of date.
             */
            p =
                    Runtime.getRuntime().exec(getSvnExecutable() + " log --xml -r HEAD .",
                            null, new File("."));
            Utils.sinkStream(p.getErrorStream());
            String remoteString = null;
            try
            {
                doc = new SAXBuilder().build(p.getInputStream());
                exitCode = p.waitFor();
                if (exitCode != 0)
                    throw new RuntimeException("Remote subversion server info returned "
                        + "exit code " + exitCode + "; expected 0");
                Attribute att =
                        (Attribute) XPath.selectSingleNode(doc.getRootElement(),
                                "/log/logentry/@revision");
                long remoteRevision = Long.parseLong(att.getValue());
                remoteString =
                        "The latest version available is JZBot revision " + remoteRevision;
                if (remoteRevision == latestLocalRevision)
                    remoteString += ", which means I'm up-to-date.";
                else if (remoteRevision > latestLocalRevision)
                    remoteString +=
                            ", which means there's a newer version of "
                                + "JZBot available. Use the \"update\" command to instruct "
                                + "me to automatically download and install that version.";
                else
                    remoteString +=
                            ", which means your version of JZBot is newer "
                                + "than the newest version available from the update "
                                + "server. This usually means something's wrong.";
            }
            catch (Exception e)
            {
                e.printStackTrace();
                remoteString =
                        "I couldn't connect to the subversion update server (because of "
                            + JZBot.pastebinStack(e)
                            + ", so I don't know if I'm up-to-date or not.";
            }
            /*
             * Now we send everything back to the user.
             */
            source.sendSpaced("I'm running JZBot revision " + latestLocalRevision + ". "
                + remoteString);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Exception occurred while trying to "
                + "get subversion info", e);
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
    
    public static String getSvnExecutable()
    {
        if (System.getProperty("os.name").toLowerCase().contains("windows"))
            return "lib/jsvn.bat";
        else
            return "lib/jsvn";
    }
    
    @Override
    public boolean relevant(String server, String channel, boolean pm, UserMessenger sender,
            Messenger source, String arguments)
    {
        return true;
    }
}
