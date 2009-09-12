package org.opengroove.jzbot.commands;

import org.opengroove.jzbot.Command;
import org.opengroove.jzbot.JZBot;
import org.opengroove.jzbot.storage.Channel;
import org.opengroove.jzbot.storage.Operator;

public class ShutdownCommand implements Command
{
    
    public String getName()
    {
        return "shutdown";
    }
    
    public void run(String channel, boolean pm, String sender, String hostname,
        String arguments)
    {
        if (!JZBot.isSuperop(hostname))
        {
            JZBot.bot.sendMessage(pm ? sender : channel, "You're not a superop.");
            return;
        }
        JZBot.bot.sendMessage(pm ? sender : channel,
            "Shutdown has been scheduled. No further commands will be acknowledged.");
        long sleepDuration = 5000;
        for (Channel c : JZBot.storage.getChannels().isolate())
        {
            sleepDuration += 1100;
            JZBot.bot.sendMessage(c.getName(), sender
                + " has requested that I shut down. Later all.");
        }
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
