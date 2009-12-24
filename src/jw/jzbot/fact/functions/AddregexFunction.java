package jw.jzbot.fact.functions;

import jw.jzbot.commands.RegexCommand;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class AddregexFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        RegexCommand.doRegexCommand(context.getServer(), context.getChannel(), false,
                context.getSender(), context.getSource(), "add " + arguments.getString(0),
                false);
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{addregex||<desc>}} -- Adds a regex to the current "
                + "channel. This acts as if \"regex add <desc>\" has been run "
                + "at this channel.";
    }
    
}
