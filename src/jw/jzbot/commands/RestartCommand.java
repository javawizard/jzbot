package jw.jzbot.commands;

import jw.jzbot.Command;
import jw.jzbot.JZBot;

public class RestartCommand implements Command
{
    
    @Override
    public String getName()
    {
        return "restart";
    }
    
    @Override
    public void run(String channel, boolean pm, String sender, String hostname,
            String arguments)
    {
        JZBot.verifySuperop(hostname);
        JZBot.bot.sendMessage(pm ? sender : channel,
                "The bot will be restarted shortly. Note that this only "
                        + "works if you used the jzbot wrapper script to start "
                        + "the bot. If you didn't, the bot will shut down instead.");
        long sleepDuration = 1000;
        try
        {
            Thread.sleep(sleepDuration);
        }
        catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.exit(17);
    }
    
}
