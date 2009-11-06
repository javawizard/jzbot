package jw.jzbot.fact.functions;

import jw.jzbot.JZBot;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.storage.Channel;

public class TriggerFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        Channel c = JZBot.storage.getChannel(context.getChannel());
        if (c == null)
            return "";
        if (arguments.length() == 0)
            return c.getTrigger();
        c.setTrigger(arguments.get(0));
        return "";
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{trigger}} or {{trigger||<new>}} -- When invoked as {{trigger}}, "
                + "evaluates to the current channel's trigger. When invoked as "
                + "{{trigger||<new>}}, sets the current channel's trigger to <new>. "
                + "If there isn't a current channel (IE this is a global factoid and "
                + "the user pm'ed us), then this does nothing and evaluates to nothing.";
    }
    
}
