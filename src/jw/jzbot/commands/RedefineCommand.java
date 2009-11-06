package jw.jzbot.commands;

import java.util.ArrayList;

import jw.jzbot.Command;
import jw.jzbot.JZBot;
import jw.jzbot.storage.MapEntry;
import jw.jzbot.utils.JZUtils;


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
