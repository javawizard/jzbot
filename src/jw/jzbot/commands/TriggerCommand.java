package jw.jzbot.commands;

import jw.jzbot.Command;
import jw.jzbot.JZBot;
import jw.jzbot.storage.Channel;
import jw.jzbot.storage.Server;

public class TriggerCommand implements Command
{
    
    public String getName()
    {
        return "trigger";
    }
    
    public void run(String server, String channel, boolean pm, String sender,
            String hostname, String arguments)
    {
        if (channel == null)
        {
            JZBot.getServer(server).sendMessage(pm ? sender : channel,
                    "You need to run the trigger command in the context of a channel.");
            return;
        }
        Server s = JZBot.storage.getServer(server);
        Channel c = s.getChannel(channel);
        if (arguments.equals(""))
        {
            JZBot.getServer(server).sendMessage(pm ? sender : channel,
                    "The current trigger is " + c.getTrigger());
            return;
        }
        if (!JZBot.isSuperop(server, hostname))
        {
            JZBot.getServer(server).sendMessage(pm ? sender : channel,
                    "You are not an op here, so you can't change the trigger");
            return;
        }
        c.setTrigger(arguments);
        JZBot.getServer(server).sendMessage(pm ? sender : channel,
                "Trigger set to " + arguments);
    }
    
}
