package jw.jzbot.commands.d;

import jw.jzbot.Command;
import jw.jzbot.JZBot;

public class LengthCommand implements Command
{
    
    @Override
    public String getName()
    {
        return "length";
    }
    
    @Override
    public void run(String channel, boolean pm, String sender, String hostname,
            String arguments)
    {
        JZBot.bot.sendMessage(pm ? sender : channel, "" + arguments.length());
    }
}
