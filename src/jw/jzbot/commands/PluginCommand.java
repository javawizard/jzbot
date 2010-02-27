package jw.jzbot.commands;

import java.security.SecureRandom;

import jw.jzbot.Command;
import jw.jzbot.Messenger;
import jw.jzbot.ResponseException;
import jw.jzbot.ServerUser;

public class PluginCommand implements Command
{
    private static final int KEY_LENGTH = 32;
    private static SecureRandom random = new SecureRandom();
    
    @Override
    public String getName()
    {
        return "plugin";
    }
    
    @Override
    public void run(String server, String channel, boolean pm, ServerUser sender,
            Messenger source, String arguments)
    {
        String[] split = arguments.split(" ", 2);
        String command = split[0];
        if (command.equals(""))
            throw new ResponseException("You need to specify a command to run. Try one of "
                + "newkey, listkeys, deletekey, getkey, load, unload, list, loaded.");
        else if (command.equals(""))
        {
            
        }
        else
            throw new ResponseException("Invalid command. Try just the "
                + "plugin command without any arguments to see "
                + "a list of allowed commands.");
    }
    
    public static String generateKey()
    {
        StringBuffer key = new StringBuffer();
        while (key.length() < KEY_LENGTH)
        {
            key.append(Integer.toHexString(random.nextInt(Integer.MAX_VALUE - 7)));
        }
        return key.substring(0, KEY_LENGTH);
    }
    
}
