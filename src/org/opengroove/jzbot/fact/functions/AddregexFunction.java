package org.opengroove.jzbot.fact.functions;

import org.opengroove.jzbot.commands.RegexCommand;
import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.Function;

public class AddregexFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        RegexCommand.doRegexCommand(context.getChannel(), false, context
                .getSender(), "add " + arguments.get(0), false);
        return "";
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{addregex||<desc>}} -- Adds a regex to the current "
                + "channel. This acts as if \"regex add <desc>\" has been run "
                + "at this channel.";
    }
    
}
