package jw.jzbot.commands;

import jw.jzbot.Command;
import jw.jzbot.JZBot;
import jw.jzbot.Messenger;
import jw.jzbot.ResponseException;
import jw.jzbot.ServerUser;
import jw.jzbot.storage.Channel;
import jw.jzbot.storage.Server;

public class JoinCommand implements Command
{
    
    public String getName()
    {
        return "join";
    }
    
    public void run(String server, String channel, boolean pm, ServerUser sender,
            Messenger source, String arguments)
    {
        Server dServer = JZBot.storage.getServer(server);
        // if (!pm)
        // {
        // JZBot.bot.sendMessage(sender, "Join only works in a pm");
        // return;
        // }
        sender.verifySuperop();
        String name = arguments;
        if (dServer.getChannel(name) != null)
        {
            Channel c = dServer.getChannel(name);
            if (c.isSuspended())
            {
                c.setSuspended(false);
                source.sendMessage("Ok, I'll come back to that channel. I remember all "
                        + "of my factoids and such from there.");
                JZBot.getServer(server).joinChannel(name);
                // JZBot.bot.sendMessage(name, "I've come back (courtesy of " + sender
                // + ")");
                // JZBot.bot.sendMessage(name, "I still remember all of my factoids and "
                // + "settings from this channel, and I'm still only "
                // + "allowing ops to create factoids here.");
            }
            else
            {
                throw new ResponseException("I'm already a member of that channel. If "
                        + "I seem to have left it, you could try sending \"restart\" in "
                        + "a pm to me to get me to restart.");
            }
            return;
        }
        Channel c = JZBot.storage.createChannel();
        c.setName(name);
        c.setTrigger("~");
        dServer.getChannels().add(c);
        JZBot.getServer(server).joinChannel(name);
        sender.sendMessage("Successful. I'm only allowing superops to create stuff here.");
        // JZBot.bot.sendMessage(name, "Here I am (courtesy of " + sender
        // + "). I'm only allowing ops to create factoids here.");
    }
}
