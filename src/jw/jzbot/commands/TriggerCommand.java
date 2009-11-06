package jw.jzbot.commands;

import jw.jzbot.Command;
import jw.jzbot.JZBot;
import jw.jzbot.storage.Channel;

public class TriggerCommand implements Command
{
    
    public String getName()
    {
        return "trigger";
    }
    
    public void run(String channel, boolean pm, String sender, String hostname,
        String arguments)
    {
        if (channel == null)
        {
            JZBot.bot.sendMessage(pm ? sender : channel, "Need a channel");
            return;
        }
        Channel c = JZBot.storage.getChannel(channel);
        if (arguments.equals(""))
        {
            JZBot.bot.sendMessage(pm ? sender : channel, "The current trigger is "
                + c.getTrigger());
            return;
        }
        if (!JZBot.isOp(channel, hostname))
        {
            JZBot.bot.sendMessage(pm ? sender : channel,
                "You are not an op here, so you can't change the trigger");
            return;
        }
        c.setTrigger(arguments);
        JZBot.bot.sendMessage(pm ? sender : channel, "Trigger set to " + arguments);
    }
    
}
