package jw.jzbot.commands;

import java.security.SecureRandom;

import net.sf.opengroove.common.utils.StringUtils;

import jw.jzbot.Command;
import jw.jzbot.FactScope;
import jw.jzbot.JZBot;
import jw.jzbot.Messenger;
import jw.jzbot.ResponseException;
import jw.jzbot.ServerUser;
import jw.jzbot.plugins.PluginSystem;
import jw.jzbot.storage.PersistentKey;

public class PluginCommand implements Command
{
    
    @Override
    public String getName()
    {
        return "plugin";
    }
    
    @Override
    public void run(String server, String channel, boolean pm, ServerUser sender,
            Messenger source, String arguments)
    {
        String[] argumentsTokenized1 = arguments.split(" ", 2);
        String command = argumentsTokenized1[0];
        String afterCommand =
                (argumentsTokenized1.length > 1) ? argumentsTokenized1[1] : "";
        afterCommand = "";
        if (command.equals("available"))
        {
            String response =
                    "The following plugins are currently installed. "
                        + "This list does not include plugins whose language "
                        + "support plugin is not currently active. The plugins are: ";
            response += StringUtils.delimited(PluginSystem.knownPluginNames, " ");
            if (response.length() > (source.getProtocolDelimitedLength() * 2))
                response = JZBot.pastebinNotice(response, null);
            source.sendSpaced(response);
        }
        else if (command.equals("enabled"))
        {
            
        }
        else if (command.equals("active"))
        {
            
        }
        else
        {
            throw new ResponseException("Invalid plugin command. Try 'plugin "
                + "<available|enabled|active>'");
        }
    }
}
