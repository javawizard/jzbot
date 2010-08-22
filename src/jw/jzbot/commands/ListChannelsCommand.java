package jw.jzbot.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.opengroove.common.utils.StringUtils;

import jw.jzbot.Command;
import jw.jzbot.ConnectionWrapper;
import jw.jzbot.JZBot;
import jw.jzbot.Messenger;
import jw.jzbot.ResponseException;
import jw.jzbot.ServerUser;
import jw.jzbot.storage.Channel;
import jw.jzbot.storage.Server;

public class ListChannelsCommand implements Command
{
    
    @Override
    public String getName()
    {
        return "listchannels";
    }
    
    @Override
    public void run(String server, String channel, boolean pm, ServerUser sender,
            Messenger source, String arguments)
    {
        if (server == null)
            throw new ResponseException("This command must be run in the context of "
                + "a server.");
        ConnectionWrapper connection = JZBot.getConnection(server);
        Server s = JZBot.storage.getServer(server);
        source.sendSpaced("Here's the list. Channels in this list are in the format "
            + "<flags>:<name>, where <flags are some flags and <name> is the name "
            + "of the channel. Flags are 1: currently joined, 2: in storage, 3: "
            + "set to autojoin, 0: no other flags");
        Set<String> channelNameSet = new HashSet<String>();
        // First, we'll add all of the channels that the bot is joined to.
        if (connection != null)
            channelNameSet.addAll(Arrays.asList(connection.getConnection().getChannels()));
        // Now we add all of the channels that the bot knows about in storage.
        if (s != null)
        {
            for (Channel c : s.getChannels().isolate())
            {
                channelNameSet.add(c.getName());
            }
        }
        List<String> channelNames = new ArrayList<String>(channelNameSet);
        Collections.sort(channelNames);
        // Now we bulid the list of items.
        List<String> items = new ArrayList<String>();
        for (String channelName : channelNames)
        {
            String flags = "";
            if (connection != null
                && Arrays.asList(connection.getConnection().getChannels()).contains(
                        channelName))
                flags += "1";
            if (s != null)
            {
                Channel c = s.getChannel(channelName);
                if (c != null)
                {
                    flags += "2";
                    if (!c.isSuspended())
                        flags += "3";
                }
            }
            if (flags.length() == 0)
                flags = "0";
            items.add(flags + ":" + channelName);
        }
        source.sendSpaced(StringUtils.delimited(items.toArray(new String[0]), "  "));
    }

    @Override
    public boolean relevant(String server, String channel, boolean pm, ServerUser sender,
            Messenger source, String arguments)
    {
        return true;
    }
}
