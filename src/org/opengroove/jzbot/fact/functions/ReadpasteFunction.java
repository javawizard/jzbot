package org.opengroove.jzbot.fact.functions;

import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.Function;
import org.opengroove.jzbot.utils.Pastebin;

public class ReadpasteFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        return Pastebin.readPost(arguments.get(0));
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{readpaste||<pasteurl>}} -- Reads a pastebin post made "
                + "at http://pastebin.com. Currently, this only supports reads from "
                + "pastebin.com itself, and does not support reads from custom pastebin "
                + "domains (like http://java.pastebin.com). I'm planning on adding such "
                + "support in a future version of the bot.";
    }
    
}
