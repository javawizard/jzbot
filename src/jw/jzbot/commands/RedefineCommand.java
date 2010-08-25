package jw.jzbot.commands;

import java.util.ArrayList;

import jw.jzbot.Command;
import jw.jzbot.JZBot;
import jw.jzbot.Messenger;
import jw.jzbot.ResponseException;
import jw.jzbot.ServerUser;
import jw.jzbot.storage.MapEntry;
import jw.jzbot.utils.Utils;

public class RedefineCommand implements Command
{
    
    @Override
    public String getName()
    {
        return "redefine";
    }
    
    @Override
    public void run(String server, String channel, boolean pm, ServerUser sender,
            Messenger source, String arguments)
    {
        // NOTE: anyone should be allowed to read the redefinition list, but only superops
        // should be able to modify it.
        String[] tokens = arguments.split(" ");
        if (arguments.equals(""))
        {
            ArrayList<String> list = new ArrayList<String>();
            list.add("Use ~redefine <command> <newname> to redefine <command> to have "
                    + "the name <newname>. Current redefinitions (newname->command):");
            for (MapEntry redefinition : JZBot.storage.getRedefinitions())
            {
                list.add(redefinition.getKey() + "->" + redefinition.getValue());
            }
            Utils.ircSendDelimited(list.toArray(new String[0]), "  ", source);
        }
        throw new ResponseException(
                "Redefinition is not yet supported. When it is, it will "
                        + "allow changing the names that built-in commands "
                        + "(except for ~redefine itself) are known under.");
    }

    @Override
    public boolean relevant(String server, String channel, boolean pm, ServerUser sender,
            Messenger source, String arguments)
    {
        return true;
    }
    
}
