package jw.jzbot.commands;

import jw.jzbot.Command;
import jw.jzbot.JZBot;

public class CommandListCommand implements Command
{
    
    public String getName()
    {
        return "commandlist";
    }
    
    public void run(String server, String channel, boolean pm, String sender,
            String hostname, String arguments)
    {
        String currentList = "";
        for (String name : JZBot.commands.keySet())
        {
            currentList += name + "  ";
            if (currentList.length() > 200)
            {
                JZBot.getServer(server).sendMessage(pm ? sender : channel, currentList);
                currentList = "";
            }
        }
        if (!currentList.equals(""))
            JZBot.getServer(server).sendMessage(pm ? sender : channel, currentList);
        JZBot.getServer(server).sendMessage(pm ? sender : channel, "End of command list");
    }
    
}
