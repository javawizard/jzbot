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
        else if (command.equals("newkey"))
        {
            if (split.length != 2)
                throw new ResponseException("Syntax: newkey <name>, "
                    + "where <name> is the name to use for plugins that authenticate "
                    + "with this key.");
            if (!split[1].matches("^[\\.\\-\\_a-zA-Z0-9]+$"))
                throw new ResponseException("Plugin names have to "
                    + "be at least one character in length and can contain only "
                    + "letters, numbers, hyphens, period characters, and underscores.");
            if (JZBot.storage.getPluginKeyByName(split[1]) != null)
                throw new ResponseException("There's already a key with that plugin name.");
            String newKey = generateKey();
            PersistentKey persistentKey = JZBot.storage.createPersistentKey();
            persistentKey.setKey(newKey);
            persistentKey.setName(split[1]);
            JZBot.storage.getPluginKeys().add(persistentKey);
            source.sendMessage("A new key has been added with the plugin name \""
                + split[1] + "\". The key is " + newKey);
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
