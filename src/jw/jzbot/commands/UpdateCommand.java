package jw.jzbot.commands;

import java.io.File;
import java.io.InputStream;

import jw.jzbot.Command;
import jw.jzbot.ResponseException;
import jw.jzbot.scope.Messenger;
import jw.jzbot.scope.UserMessenger;
import jw.jzbot.utils.Utils;
import net.sf.opengroove.common.utils.StringUtils;

public class UpdateCommand implements Command
{
    private static volatile boolean startedUpdates = false;
    
    @Override
    public String getName()
    {
        return "update";
    }
    
    @Override
    public void run(String server, String channel, boolean pm, UserMessenger sender,
            Messenger source, String arguments)
    {
        sender.verifySuperop();
        startUpdates(source);
    }
    
    public static synchronized void startUpdates(final Messenger source)
    {
        if (startedUpdates)
            throw new ResponseException("So that one time I told you not to restart me... " +
                    "Don't do that either.");
        String scriptName = getAutoUpdateScriptName();
        if (scriptName == null)
            throw new ResponseException("You're running your bot on "
                + "a system that doesn't currently support automatic "
                + "updates. Visit us at github.com/javawizard/jzbot for help with this.");
        try
        {
            final Process p = Runtime.getRuntime().exec(new String[] { scriptName });
            InputStream in = p.getInputStream();
            InputStream err = p.getErrorStream();
            Utils.sinkStream(in);
            Utils.sinkStream(err);
            new Thread("update-check-waiter")
            {
                public void run()
                {
                    try
                    {
                        p.waitFor();
                    }
                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }
                    if (p.exitValue() == 0) {
                        if (source != null) {
                            File f = new File("storage/update-log");
                            if (f.exists() && StringUtils.readFile(f).trim().length() > 0) {
                                source.sendMessage("Updated. Changes:");
                                String[] lines = StringUtils.readFile(f).trim().split("\n");
                                int i = 0;
                                for (String line : lines) {
                                    if (i == 5) {
                                        source.sendMessage("...and " + (lines.length - 5) + " more");
                                        break;
                                    }
                                    i += 1;
                                    source.sendMessage(line);
                                }
                                source.sendMessage("Restarting, be back in a moment.");
                            } else {
                                source.sendMessage("Doesn't look like there were any updates. " +
                                        "Restarting just in case...");
                            }
                        }
                    } else {
                        if (source != null)
                            source.sendMessage("Updates failed. You'll need to run ./update from the install " +
                                    "directory manually and see what happens.");
                    }
                }
            }.start();
        }
        catch (Exception e)
        {
            throw new RuntimeException("Exception occurred while starting "
                + "the update process. The update process may still have "
                + "started successfully, so be very careful about "
                + "restarting until you're sure it failed.", e);
        }
        startedUpdates = true;
        source.sendMessage("Updating... (don't restart me)");
    }
    
    private static String getAutoUpdateScriptName()
    {
        File file = null;
        String osName = System.getProperty("os.name");
        osName = osName.toLowerCase();
        if (osName.contains("linux"))
            file = new File("update");
        if (osName.contains("mac"))
            file = new File("update");
        if (osName.contains("windows"))
            file = new File("update.bat");
        if (file == null)
            return null;
        if (!file.exists())
            return null;
        return file.getAbsolutePath();
    }
    
    @Override
    public boolean relevant(String server, String channel, boolean pm, UserMessenger sender,
            Messenger source, String arguments)
    {
        return true;
    }
}
