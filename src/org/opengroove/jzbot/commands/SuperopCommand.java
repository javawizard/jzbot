package org.opengroove.jzbot.commands;

import java.util.ArrayList;

import org.opengroove.jzbot.Command;
import org.opengroove.jzbot.JZBot;
import org.opengroove.jzbot.storage.Operator;

public class SuperopCommand implements Command
{
    
    public String getName()
    {
        return "superop";
    }
    
    public void run(String channel, boolean pm, String sender, String hostname,
            String arguments)
    {
        JZBot.bot.verifySuperop(hostname);
        String[] tokens = arguments.split(" ", 2);
        String subcommand = tokens[0];
        if (subcommand.equals("list"))
        {
            JZBot.bot.sendMessage(pm ? sender : channel,
                    "Start of superop list by hostname");
            ArrayList<String> strings = new ArrayList<String>();
            for (Operator op : JZBot.storage.getOperators().isolate())
            {
                strings.add(op.getHostname());
            }
            JZBot.sendDelimited(strings.toArray(new String[0]), "    ",
                    pm ? sender : channel);
            JZBot.bot.sendMessage(pm ? sender : channel, "End of superop list");
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
            JZBot.storage.getOperators().add(op);
            JZBot.bot.sendMessage(pm ? sender : channel, "Hostname "
                    + newHostname + " was successfully added as a superop.");
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
            Operator op = JZBot.storage.getOperator(newHostname);
            if (op == null)
            {
                JZBot.bot.sendMessage(pm ? sender : channel,
                        "That hostname isn't a superop.");
                return;
            }
            JZBot.storage.getOperators().remove(op);
            JZBot.bot.sendMessage(pm ? sender : channel, "Removed.");
        }
        else
        {
            JZBot.bot.sendMessage(pm ? sender : channel,
                    "Specify one of add, list, or delete.");
            return;
        }
        
    }
    
}
