package org.opengroove.jzbot.commands;

import net.sf.opengroove.common.utils.StringUtils;

import org.opengroove.jzbot.Command;
import org.opengroove.jzbot.JZBot;
import org.opengroove.jzbot.fact.FactParser;
import org.opengroove.jzbot.fact.Function;

public class FunctionsCommand implements Command
{
    
    @Override
    public String getName()
    {
        return "functions";
    }
    
    @Override
    public void run(String channel, boolean pm, String sender, String hostname,
            String arguments)
    {
        if (arguments.trim().equals(""))
        {
            JZBot.bot
                    .sendMessage(pm ? sender : channel,
                            "Functions: (use \"~functions <name>\" to get help on a particular function) ");
            JZBot.sendDelimited(FactParser.getFunctionNames(), "   ",
                    pm ? sender : channel);
        }
        else
        {
            Function function = FactParser.getFunction(arguments);
            if (function == null)
            {
                JZBot.bot.sendMessage(pm ? sender : channel,
                        "No such function: " + function);
                return;
            }
            String help = function.getHelp(null);
            if (help == null)
                help = "No help for this function is currently available";
            String[] parsed = help.split("\n");
            for (String s : parsed)
            {
                JZBot.bot.sendMessage(pm ? sender : channel, s);
            }
        }
    }
}
