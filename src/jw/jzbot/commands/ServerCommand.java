package jw.jzbot.commands;

import jw.jzbot.Command;

public class ServerCommand implements Command
{
    
    @Override
    public String getName()
    {
        return "server";
    }
    
    @Override
    public void run(String server, String channel, boolean pm, String sender,
            String hostname, String arguments)
    {
    }
    
}
