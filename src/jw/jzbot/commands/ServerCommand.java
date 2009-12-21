package jw.jzbot.commands;

import jw.jzbot.Command;
import jw.jzbot.JZBot;

public class ServerCommand implements Command
{
    
    @Override
    public String getName()
    {
        return "server";
    }
    
    @Override
    public void run(String server, String channel, boolean pm, String sender,
            String hostname, String arguments)
    {
        // TODO: make a config var that, when set to a certain value, allows the list of
        // servers to be read by non-superops, too.
        JZBot.verifySuperop(server, hostname);
        if (arguments.equals(""))
        {
            JZBot.getServer(server).sendMessage(
                    pm ? sender : channel,
                    "You need to specify one of add, delete, activate, "
                            + "deactivate, or edit.");
            return;
        }
    }
    
}
