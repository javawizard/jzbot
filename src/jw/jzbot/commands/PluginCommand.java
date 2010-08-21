package jw.jzbot.commands;

import java.security.SecureRandom;

import jw.jzbot.Command;
import jw.jzbot.JZBot;
import jw.jzbot.Messenger;
import jw.jzbot.ResponseException;
import jw.jzbot.ServerUser;
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
        throw new ResponseException("This command still needs to be written. "
            + "And note to jcp: write PythonLanguageSupport.java too.");
        TODO: ^
    }
    
}
