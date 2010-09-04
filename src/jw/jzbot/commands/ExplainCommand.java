package jw.jzbot.commands;

import jw.jzbot.Command;
import jw.jzbot.JZBot;
import jw.jzbot.ResponseException;
import jw.jzbot.fact.FactParser;
import jw.jzbot.scope.Messenger;
import jw.jzbot.scope.UserMessenger;
import jw.jzbot.storage.Factoid;
import jw.jzbot.utils.Pastebin;
import jw.jzbot.utils.Pastebin.Duration;

public class ExplainCommand implements Command
{
    
    @Override
    public String getName()
    {
        return "explain";
    }
    
    @Override
    public void run(String server, String channel, boolean pm, UserMessenger sender,
            Messenger source, String arguments)
    {
        // FIXME: merge this into ~factoid, to allow for scoping (IE specifying
        // specifically which factoid you want if there are several factoids with the same
        // name at different scopes)
        if (arguments.equals(""))
        {
            throw new ResponseException("You need to specify the name of a factoid.");
        }
        Factoid f = null;
        if (channel != null)
            f =
                    JZBot.getChannelFactoid(JZBot.storage.getServer(server), channel,
                            arguments);
        if (f == null)
            f = JZBot.getServerFactoid(JZBot.storage.getServer(server), arguments);
        if (f == null)
            f = JZBot.getGlobalFactoid(arguments);
        if (f == null)
            throw new ResponseException("No such factoid: " + arguments);
        String explanation = FactParser.explain(f.getValue(), f.getName());
        StringBuffer buffer = new StringBuffer();
        buffer.append("Factoid " + f.getName() + ": " + f.getValue());
        buffer.append("\n\nExplanation for this factoid:\n\n");
        buffer.append(explanation);
        source.sendMessage("Explanation of this factoid: "
            + Pastebin.createPost("jzbot", buffer.toString(), Duration.DAY, null, null));
    }
    
    @Override
    public boolean relevant(String server, String channel, boolean pm, UserMessenger sender,
            Messenger source, String arguments)
    {
        return true;
    }
}
