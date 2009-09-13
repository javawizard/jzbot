package org.opengroove.jzbot.commands;

import net.sf.opengroove.common.utils.StringUtils;

import org.opengroove.jzbot.Command;
import org.opengroove.jzbot.JZBot;
import org.opengroove.jzbot.fact.FactParser;

public class ListFunctionsCommand implements Command
{
    
    @Override
    public String getName()
    {
        return "listfunctions";
    }
    
    @Override
    public void run(String channel, boolean pm, String sender, String hostname,
            String arguments)
    {
        JZBot.bot.sendMessage(pm ? sender : channel, StringUtils.delimited(
                FactParser.getFunctionNames(), "  "));
    }
}
