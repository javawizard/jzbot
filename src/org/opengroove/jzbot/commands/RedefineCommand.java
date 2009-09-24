package org.opengroove.jzbot.commands;

import java.util.ArrayList;

import org.opengroove.jzbot.Command;
import org.opengroove.jzbot.JZBot;
import org.opengroove.jzbot.storage.MapEntry;
import org.opengroove.jzbot.utils.JZUtils;

public class RedefineCommand implements Command
{
    
    @Override
    public String getName()
    {
        return "redefine";
    }
    
    @Override
    public void run(String channel, boolean pm, String sender, String hostname,
            String arguments)
    {
        String[] tokens = arguments.split(" ");
        if (arguments.equals(""))
        {
            ArrayList<String> list = new ArrayList<String>();
            list
                    .add("Use ~redefine <command> <newname> to redefine <command> to have "
                            + "the name <newname>. Current redefinitions (newname->command):");
            for (MapEntry redefinition : JZBot.storage.getRedefinitions())
            {
                list
                        .add(redefinition.getKey() + "->"
                                + redefinition.getValue());
            }
            JZUtils.ircSendDelimited(list.toArray(new String[0]), "  ",
                    JZBot.bot, pm ? sender : channel);
        }
    }
    
}
