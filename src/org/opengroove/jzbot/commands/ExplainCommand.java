package org.opengroove.jzbot.commands;

import org.opengroove.jzbot.Command;
import org.opengroove.jzbot.JZBot;
import org.opengroove.jzbot.ResponseException;
import org.opengroove.jzbot.fact.FactParser;
import org.opengroove.jzbot.storage.Factoid;
import org.opengroove.jzbot.utils.Pastebin;
import org.opengroove.jzbot.utils.Pastebin.Duration;

public class ExplainCommand implements Command
{
    
    @Override
    public String getName()
    {
        return "explain";
    }
    
    @Override
    public void run(String channel, boolean pm, String sender, String hostname,
            String arguments)
    {
        if (arguments.equals(""))
        {
            throw new ResponseException(
                    "You need to specify the name of a factoid.");
        }
        Factoid f = null;
        if (channel != null)
            f = JZBot.getChannelFactoid(channel, arguments);
        if (f == null)
            f = JZBot.getGlobalFactoid(arguments);
        if (f == null)
            throw new ResponseException("No such factoid: " + arguments);
        String explanation = FactParser.explain(f.getValue());
        StringBuffer buffer = new StringBuffer();
        buffer.append("Factoid " + f.getName() + ": " + f.getValue());
        buffer.append("\n\nExplanation for this factoid:\n\n");
        buffer.append(explanation);
        JZBot.bot.sendMessage(pm ? sender : channel,
                "Explanation of this factoid: http://pastebin.com/"
                        + Pastebin.createPost("jzbot", buffer.toString(),
                                Duration.DAY, ""));
    }
}
