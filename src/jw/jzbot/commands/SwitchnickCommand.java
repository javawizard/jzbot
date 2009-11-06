package jw.jzbot.commands;

import jw.jzbot.Command;
import jw.jzbot.JZBot;
import jw.jzbot.ResponseException;

import org.jibble.pircbot.PircBot;

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
