package jw.jzbot.commands;

import java.io.File;
import java.io.InputStream;

import jw.jzbot.Command;
import jw.jzbot.Messenger;
import jw.jzbot.ResponseException;
import jw.jzbot.ServerUser;
import jw.jzbot.utils.JZUtils;

public class UpdateCommand implements Command
{
    private static volatile boolean startedUpdates = false;
    
    @Override
    public String getName()
    {
        return "update";
    }
    
    @Override
    public void run(String server, String channel, boolean pm, ServerUser sender,
            Messenger source, String arguments)
    {
        sender.verifySuperop();
        startUpdates(source);
    }
    
    public static synchronized void startUpdates(final Messenger source)
    {
        if (startedUpdates)
            throw new ResponseException("Updates are currently being "
                + "downloaded and installed.");
        String scriptName = getAutoUpdateScriptName();
        if (scriptName == null)
            throw new ResponseException("You're running your bot on "
                + "a system that doesn't currently support automatic "
                + "updates. Visit us at jzbot.googlecode.com for help with this.");
        try
        {
            final Process p = Runtime.getRuntime().exec(new String[] { scriptName });
            InputStream in = p.getInputStream();
            InputStream err = p.getErrorStream();
            JZUtils.sinkStream(in);
            JZUtils.sinkStream(err);
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
                    if (source != null)
                        source.sendMessage("Updates finished successfully. "
                            + "The bot will restart itself in a few moments.");
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
        source.sendMessage("Updates have been started. The bot will "
            + "automatically restart once it has finished updating. "
            + "Do not attempt to restart the bot in any other way "
            + "until it restarts itself. This might take a few minutes, "
            + "and some commands and functionality may not work while "
            + "the update is going on.");
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
    public boolean relevant(String server, String channel, boolean pm, ServerUser sender,
            Messenger source, String arguments)
    {
        return true;
    }
}
