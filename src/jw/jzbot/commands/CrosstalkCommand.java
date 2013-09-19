package jw.jzbot.commands;

import jw.jzbot.Command;
import jw.jzbot.crosstalk.Crosstalk;
import jw.jzbot.scope.Messenger;
import jw.jzbot.scope.UserMessenger;

public class CrosstalkCommand implements Command
{
    
    @Override
    public String getName()
    {
        return "__crosstalk__";
    }
    
    @Override
    public boolean relevant(String server, String channel, boolean pm,
            UserMessenger sender, Messenger source, String arguments)
    {
        return true;
    }
    
    @Override
    public void run(String server, String channel, boolean pm, UserMessenger sender,
            Messenger source, String arguments)
    {
        Crosstalk.run(server, channel, pm, sender, source, arguments);
    }
    
}
