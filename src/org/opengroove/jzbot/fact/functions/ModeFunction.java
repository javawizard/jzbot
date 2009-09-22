package org.opengroove.jzbot.fact.functions;

import org.opengroove.jzbot.JZBot;
import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.Function;

public class ModeFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        JZBot.bot.setMode(context.getChannel(), arguments.get(0));
        return "";
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{mode||<arguments>}} -- Sets the specified mode on the channel. This "
                + "must only contain one mode for now. For example, to make the person "
                + "\"jcp\" an op, you could use {{mode||+o jcp}}. To take away ops from jcp, "
                + "you could use {{mode||-o jcp}}. To add color lock to the channel (a "
                + "hyperion-specific mode), you could use {{mode||+c}}.";
    }
    
    public String getName()
    {
        return "mode";
    }
    
}
