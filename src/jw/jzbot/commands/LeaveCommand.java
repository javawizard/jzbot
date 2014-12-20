package jw.jzbot.commands;

import jw.jzbot.Command;
import jw.jzbot.JZBot;
import jw.jzbot.ResponseException;
import jw.jzbot.scope.Messenger;
import jw.jzbot.scope.UserMessenger;
import jw.jzbot.storage.Channel;
import jw.jzbot.storage.Server;

import org.jibble.pircbot.User;

/**
 * Leave instructs the bot to depart from a channel and not come back until a superop
 * requests that it come back. It can be run by superops only.
 * 
 * @author Alexander Boyd
 * 
 */
public class LeaveCommand implements Command
{
    
    public String getName()
    {
        return "leave";
    }
    
    public void run(String server, String channel, boolean pm, UserMessenger sender,
            Messenger source, String arguments)
    {
        Server dServer = JZBot.storage.getServer(server);
        if (channel == null)
            throw new ResponseException("You must specify a channel.");
        try
        {
            sender.verifySuperop();
        }
        catch (RuntimeException e)
        {
            if (server.equals(sender.getServerName()))
            {
                User user =
                        JZBot.getUser(JZBot.getRealConnection(server).getConnection(),
                                channel, sender.getNick());
                if (!user.isOp())
                    throw e;
            }
            else
            {
                throw e;
            }
        }
        Channel c = dServer.getChannel(channel);
        if (c == null)
            throw new ResponseException("I'm not a member of that channel. You might "
                + "want to pm \"restart\" if I'm joined there for some reason.");
        if (c.isSuspended())
            throw new ResponseException("I've already left that channel. You might "
                + "want to pm \"restart\" if I seem not to have left it.");
        c.setSuspended(true);
        source.sendSpaced("Ok, I'll leave now. I'll remember this channel's "
            + "settings, though. Use \"/msg "
            + JZBot.getServer(server).getConnection().getNick() + " join " + channel
            + "\" to have me join the channel again.");
        JZBot.getServer(server).partChannel(channel,
                "Leaving on request from " + sender.nick());
    }

    @Override
    public boolean relevant(String server, String channel, boolean pm, UserMessenger sender,
            Messenger source, String arguments)
    {
        return true;
    }
    
}
