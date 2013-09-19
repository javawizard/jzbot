package jw.jzbot.fact.functions.irc;

import jw.jzbot.JZBot;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class ModeFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        context.checkedGetConnection().getConnection().setMode(context.getChannel(),
                arguments.resolveString(0));
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {mode|<arguments>} -- Sets the specified mode on the channel. "
                + "For example, to make the person "
                + "\"jcp\" an op, you could use {mode|+o jcp}. To take away ops from jcp, "
                + "you could use {mode|-o jcp}. To add color lock to the channel (a "
                + "hyperion-specific mode), you could use {mode|+c}. If you wanted \n"
                + "to op both jcp and schrottplatz, but deop Marlen_Jackson, voice "
                + "MrDudle, add color lock, and ban *!*@1.2.3.4 all at the same time, "
                + "you could do "
                + "{mode|+oo-o+vcb jcp schrottplatz Marlen_Jackson MrDudle *!*@1.2.3.4}.";
    }
    
    public String getName()
    {
        return "mode";
    }
    
}
