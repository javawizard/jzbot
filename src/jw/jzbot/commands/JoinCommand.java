package jw.jzbot.commands;

import jw.jzbot.Command;
import jw.jzbot.JZBot;
import jw.jzbot.ResponseException;
import jw.jzbot.events.Notify;
import jw.jzbot.scope.Messenger;
import jw.jzbot.scope.ScopeLevel;
import jw.jzbot.scope.UserMessenger;
import jw.jzbot.storage.Channel;
import jw.jzbot.storage.Server;

public class JoinCommand implements Command
{
    
    public String getName()
    {
        return "join";
    }
    
    public void run(String server, String channel, boolean pm, UserMessenger sender,
            Messenger source, String arguments)
    {
        Server dServer = JZBot.storage.getServer(server);
        // if (!pm)
        // {
        // JZBot.bot.sendMessage(sender, "Join only works in a pm");
        // return;
        // }
        if (arguments.endsWith(" frominvite"))
        {
            if (!sender.isSuperop())
                throw new ResponseException("You're not a superop. Right now, I "
                    + "will only join channels when the person that invited me "
                    + "is a superop. For reference, the room you just invited "
                    + "me to is " + arguments.split(" ")[0]);
        }
        else
            sender.verifySuperop();
        String name = arguments.split(" ")[0];
        if (!name.startsWith("#"))
            throw new ResponseException("Right now, only channel names that "
                + "start with a \"#\" sign are supported.");
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
                JZBot.getServer(server).joinChannel(name);
                source
                        .sendMessage("I'm already at that channel, but I just made an attempt "
                            + "to rejoin it, just in case.");
            }
            return;
        }
        Channel c = JZBot.storage.createChannel();
        c.setName(name);
        c.setTrigger("~");
        dServer.getChannels().add(c);
        Notify.channelAdded.fireListeners(ScopeLevel.channel, "@" + dServer.getName()
            + name, false);
        JZBot.getServer(server).joinChannel(name);
        source.sendMessage("Successful. I'm only allowing superops to create stuff here.");
        // JZBot.bot.sendMessage(name, "Here I am (courtesy of " + sender
        // + "). I'm only allowing ops to create factoids here.");
    }
    
    @Override
    public boolean relevant(String server, String channel, boolean pm, UserMessenger sender,
            Messenger source, String arguments)
    {
        return true;
    }
}
