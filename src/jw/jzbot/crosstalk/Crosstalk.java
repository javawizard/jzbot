package jw.jzbot.crosstalk;

import jw.jzbot.scope.Messenger;
import jw.jzbot.scope.ScopeManager;
import jw.jzbot.scope.UserMessenger;

public class Crosstalk
{
    public static final String VERSION = "1";
    
    public static void run(String server, String channel, boolean pm, UserMessenger sender,
            Messenger source, String arguments)
    {
        if (arguments.trim().equals(""))
        {
            source.sendMessage("The __crosstalk__ command needs arguments "
                + "specified. For more help on what crosstalk is, see \"help crosstalk\".");
        }
        int originalLength = arguments.length();
        String[] tokens = arguments.split(" ", 2);
        String command = tokens[0];
        arguments = tokens[1];
    }
    
    public void startCrosstalk(String server, String channel)
    {
        
    }
}
