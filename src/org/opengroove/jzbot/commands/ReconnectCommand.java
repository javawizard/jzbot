package org.opengroove.jzbot.commands;

import org.opengroove.jzbot.Command;
import org.opengroove.jzbot.JZBot;

public class ReconnectCommand implements Command
{
    
    public String getName()
    {
        return "reconnect";
    }
    
    public void run(String channel, boolean pm, String sender, String hostname,
            String arguments)
    {
        JZBot.verifySuperop(hostname);
        JZBot.bot.sendMessage(pm ? sender : channel,
                "Reconnecting on request from " + sender);
        try
        {
            Thread.sleep(2000);
        }
        catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        JZBot.manualReconnect = true;
        JZBot.bot.disconnect();
    }
    
}
