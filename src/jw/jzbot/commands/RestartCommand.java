package jw.jzbot.commands;

import jw.jzbot.Command;
import jw.jzbot.JZBot;
import jw.jzbot.scope.Messenger;
import jw.jzbot.scope.ServerUser;

public class RestartCommand implements Command
{
    
    @Override
    public String getName()
    {
        return "restart";
    }
    
    @Override
    public void run(String server, String channel, boolean pm, ServerUser sender,
            Messenger source, String arguments)
    {
        sender.verifySuperop();
        JZBot.restart();
    }

    @Override
    public boolean relevant(String server, String channel, boolean pm, ServerUser sender,
            Messenger source, String arguments)
    {
        return true;
    }
    
}
