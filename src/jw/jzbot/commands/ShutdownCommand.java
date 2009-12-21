package jw.jzbot.commands;

import jw.jzbot.Command;
import jw.jzbot.JZBot;
import jw.jzbot.Messenger;
import jw.jzbot.ServerUser;
import jw.jzbot.storage.Channel;
import jw.jzbot.storage.Operator;

public class ShutdownCommand implements Command
{
    
    public String getName()
    {
        return "shutdown";
    }
    
    public void run(String server, String channel, boolean pm, ServerUser sender,
            Messenger source, String arguments)
    {
        // TODO: add some server-side mechanism (that can't be overridden on the
        // client-side) to prevent shutdowns. This should not prevent restarts, however;
        // only shutdowns should be prevented, to stop rogue superops from killing the bot
        // when someone that can start it back up is not present. Maybe use a storage
        // file, which blocks the shutdown command when it exists.
        sender.verifySuperop();
        source
                .sendMessage("Shutdown has been scheduled. No further commands will be acknowledged.");
        long sleepDuration = 2000;
        // for (Channel c : JZBot.storage.getChannels().isolate())
        // {
        // sleepDuration += 1100;
        // JZBot.bot.sendMessage(c.getName(), sender
        // + " has requested that I shut down. Later all.");
        // }
        try
        {
            Thread.sleep(sleepDuration);
        }
        catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.exit(0);
    }
}
