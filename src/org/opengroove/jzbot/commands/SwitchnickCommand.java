package org.opengroove.jzbot.commands;

import org.jibble.pircbot.PircBot;
import org.opengroove.jzbot.Command;
import org.opengroove.jzbot.JZBot;
import org.opengroove.jzbot.ResponseException;

public class SwitchnickCommand implements Command
{
    
    @Override
    public String getName()
    {
        return "switchnick";
    }
    
    @Override
    public void run(String channel, boolean pm, String sender, String hostname,
            String arguments)
    {
        JZBot.verifySuperop(hostname);
        if (arguments.equals(""))
        {
            throw new ResponseException("Syntax: ~switchnick <newnick>");
        }
        JZBot.bot.changeNick(arguments);
    }
    
}
