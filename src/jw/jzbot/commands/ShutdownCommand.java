package jw.jzbot.commands;

import jw.jzbot.Command;
import jw.jzbot.JZBot;
import jw.jzbot.Messenger;
import jw.jzbot.ServerUser;
import jw.jzbot.storage.Channel;
import jw.jzbot.storage.Operator;

public class ShutdownCommand implements Command
{
    
    public String getName()
    {
        return "shutdown";
    }
    
    public void run(String server, String channel, boolean pm, ServerUser sender,
            Messenger source, String arguments)
    {
        sender.verifySuperop();
        JZBot.shutdown();
    }

    @Override
    public boolean relevant(String server, String channel, boolean pm, ServerUser sender,
            Messenger source, String arguments)
    {
        return true;
    }
}
