package jw.jzbot.commands;

import jw.jzbot.Command;
import jw.jzbot.JZBot;
import jw.jzbot.pastebin.PastebinUtils;
import jw.jzbot.scope.Messenger;
import jw.jzbot.scope.UserMessenger;

public class CommandListCommand implements Command
{
    
    public String getName()
    {
        return "commandlist";
    }
    
    public void run(String server, String channel, boolean pm, UserMessenger sender,
            Messenger source, String arguments)
    {
        String currentList = "";
        for (String name : JZBot.commands.keySet())
        {
            currentList += name + "  ";
        }
        
	    if (currentList.equals(""))
	    {
	    	source.sendSpaced("No commands found.");
	    }
	    else if (currentList.length() > source.getProtocolDelimitedLength() || arguments.equals("--"))
	    {
	        currentList = PastebinUtils.pastebinNotice(currentList + "\nEnd of command list.", null);
	        source.sendSpaced("A list of all commands: " + currentList);
	    }
	    else
	    {
	    	source.sendSpaced("A list of all commands: " + currentList);
	        source.sendSpaced("End of command list");
	    }
    }
    
    @Override
    public boolean relevant(String server, String channel, boolean pm, UserMessenger sender,
            Messenger source, String arguments)
    {
        return true;
    }
}
