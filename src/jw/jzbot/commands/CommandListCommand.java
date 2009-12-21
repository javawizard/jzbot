package jw.jzbot.commands;

import jw.jzbot.Command;
import jw.jzbot.JZBot;
import jw.jzbot.ServerUser;

public class CommandListCommand implements Command
{
    
    public String getName()
    {
        return "commandlist";
    }
    
    public void run(String server, String channel, boolean pm, ServerUser sender,
            String arguments)
    {
        String currentList = "";
        for (String name : JZBot.commands.keySet())
        {
            currentList += name + "  ";
            if (currentList.length() > 200)
            {
                sender.sendMessage(server, pm, channel, currentList);
                currentList = "";
            }
        }
        if (!currentList.equals(""))
            sender.sendMessage(server, pm, channel, currentList);
        sender.sendMessage(pm, server, channel, "End of command list");
    }
}
