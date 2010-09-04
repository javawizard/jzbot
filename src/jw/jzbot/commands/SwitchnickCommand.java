package jw.jzbot.commands;

import jw.jzbot.Command;
import jw.jzbot.JZBot;
import jw.jzbot.ResponseException;
import jw.jzbot.scope.Messenger;
import jw.jzbot.scope.ServerUser;

import org.jibble.pircbot.PircBot;

public class SwitchnickCommand implements Command
{
    
    @Override
    public String getName()
    {
        return "switchnick";
    }
    
    @Override
    public void run(String server, String channel, boolean pm, ServerUser sender,
            Messenger source, String arguments)
    {
        sender.verifySuperop();
        if (arguments.equals(""))
        {
            throw new ResponseException(
                    "Syntax: ~switchnick <newnick> -- Switches the bot's nickname "
                            + "to the specified nick. This will not persist across a reconnect. "
                            + "See \"~server edit\" to change the bot's nick permanently.");
        }
        // FIXME: log this
        JZBot.getServer(server).getConnection().changeNick(arguments);
    }

    @Override
    public boolean relevant(String server, String channel, boolean pm, ServerUser sender,
            Messenger source, String arguments)
    {
        return true;
    }
    
}
