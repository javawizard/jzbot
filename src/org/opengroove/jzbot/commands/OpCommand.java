package org.opengroove.jzbot.commands;

import java.util.ArrayList;

import net.sf.opengroove.common.utils.StringUtils;

import org.opengroove.jzbot.Command;
import org.opengroove.jzbot.ConfigVars;
import org.opengroove.jzbot.JZBot;
import org.opengroove.jzbot.ResponseException;
import org.opengroove.jzbot.fact.functions.HashFunction;
import org.opengroove.jzbot.storage.Channel;
import org.opengroove.jzbot.storage.Operator;

public class OpCommand implements Command
{
    
    public String getName()
    {
        return "op";
    }
    
    public void run(String channel, boolean pm, String sender, String hostname,
            String arguments)
    {
        if (channel == null)
        {
            JZBot.bot.sendMessage(pm ? sender : channel, "You have to specify a channel.");
            return;
        }
        if (arguments.equals("elevate"))
        {
            doElevate(channel, pm, sender, hostname);
            return;
        }
        if (!JZBot.isOp(channel, hostname))
        {
            JZBot.bot.sendMessage(pm ? sender : channel, "You're not an op here.");
            return;
        }
        String[] tokens = arguments.split(" ", 2);
        String subcommand = tokens[0];
        Channel c = JZBot.storage.getChannel(channel);
        if (c == null)
        {
            JZBot.bot.sendMessage(pm ? sender : channel, "Not a channel.");
            return;
        }
        if (subcommand.equals("list"))
        {
            JZBot.bot.sendMessage(pm ? sender : channel, "Start of op list by hostname");
            ArrayList<String> strings = new ArrayList<String>();
            for (Operator op : c.getOperators().isolate())
            {
                strings.add(op.getHostname());
            }
            JZBot.sendDelimited(strings.toArray(new String[0]), "    ", pm ? sender
                    : channel);
            JZBot.bot.sendMessage(pm ? sender : channel, "End of op list");
        }
        else if (subcommand.equals("add"))
        {
            if (tokens.length == 0)
            {
                JZBot.bot.sendMessage(pm ? sender : channel,
                        "You need to specify a hostname.");
                return;
            }
            String newHostname = tokens[1];
            Operator op = JZBot.storage.createOperator();
            op.setHostname(newHostname);
            c.getOperators().add(op);
            JZBot.bot.sendMessage(pm ? sender : channel, "Hostname " + newHostname
                    + " was successfully added as an op.");
        }
        else if (subcommand.equals("delete"))
        {
            if (tokens.length == 0)
            {
                JZBot.bot.sendMessage(pm ? sender : channel,
                        "You need to specify a hostname.");
                return;
            }
            String newHostname = tokens[1];
            Operator op = c.getOperator(newHostname);
            if (op == null)
            {
                JZBot.bot.sendMessage(pm ? sender : channel, "That hostname isn't an op.");
                return;
            }
            c.getOperators().remove(op);
            JZBot.bot.sendMessage(pm ? sender : channel, "Removed.");
        }
        else if (subcommand.equals("key"))
        {
            String theKey = tokens[1];
            String theHash = HashFunction.doHash(theKey);
            if (StringUtils.isMemberOf(theHash, ConfigVars.keys.get().split("\\|")))
            {
                JZBot.elevate(hostname, ConfigVars.primary.get());
                JZBot.bot.sendMessage(pm ? sender : channel,
                        "That key is correct. Your hostname has now been "
                                + "added as a superop. This will persist until the "
                                + "bot is reconnected or restarted.");
            }
            else
            {
                throw new ResponseException("Incorrect key.");
            }
        }
        else
        {
            JZBot.bot.sendMessage(pm ? sender : channel,
                    "Specify one of add, list, delete, elevate, or key. "
                            + "Use \"~help commands op\" for help.");
            return;
        }
    }
    
    private void doElevate(String channel, boolean pm, String sender, String hostname)
    {
        if (!(ConfigVars.chanops.get().equals("1")))
            throw new ResponseException(
                    "\"~op elevate\" is not enabled. To enable it, use \"~config chanops 1\".");
        /*
         * Op elevation is enabled. Now we check to see if the user's an op at this
         * channel. If they are, we elevate them either to channel op if this channel is
         * not the primary channel, or to superop if this channel is the primary channel.
         */
        if (JZBot.isChannelOp(channel, sender) && !ConfigVars.primary.get().equals(""))
        {
            JZBot.elevate(hostname, channel);
            JZBot.bot.sendMessage(pm ? sender : channel,
                    "You have successfully been elevated.");
        }
        else
        {
            throw new ResponseException("You're not an op at this channel.");
        }
    }
}
