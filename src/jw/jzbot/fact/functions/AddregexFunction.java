package jw.jzbot.fact.functions;

import jw.jzbot.commands.RegexCommand;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;

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
