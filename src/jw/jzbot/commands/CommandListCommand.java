package jw.jzbot.commands;

import jw.jzbot.Command;
import jw.jzbot.JZBot;
import jw.jzbot.Messenger;
import jw.jzbot.ServerUser;

public class CommandListCommand implements Command
{
    
    public String getName()
    {
        return "commandlist";
    }
    
    public void run(String server, String channel, boolean pm, ServerUser sender,
            Messenger source, String arguments)
    {
        String currentList = "";
        for (String name : JZBot.commands.keySet())
        {
            currentList += name + "  ";
            if (currentList.length() > 200)
            {
                source.sendMessage(currentList);
                currentList = "";
            }
        }
        if (!currentList.equals(""))
            source.sendMessage(currentList);
        source.sendMessage("End of command list");
    }
}
