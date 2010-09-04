package jw.jzbot.fact.functions.irc;

import jw.jzbot.JZBot;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import jw.jzbot.scope.ChannelScope;
import jw.jzbot.utils.Utils;

public class SendsplitFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        String[] tokens = Utils.ircDelimited(new ChannelScope(context.getServer(),
                context.getChannel()), arguments.resolveString(3).split(
                arguments.resolveString(1)), arguments.resolveString(2));
        for (String s : tokens)
        {
            context.incrementMessageCount();
            context.checkedGetConnection().getConnection().sendMessage(
                    arguments.getString(0), s);
            try
            {
                Thread.sleep(500);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {sendsplit|<to>|<regex>|<delimiter>|<message>} -- Divides <message> "
                + "into a list of strings split around the regular expression <regex>, then "
                + "attempts to reconstruct the string, delimiting each string in the list with "
                + "<delimiter>, and send the resulting string to <to>. If the resulting string "
                + "would be longer than approximately 450 characters (IRC limits maximum line\n"
                + "length to 512 characters), then the message is split exactly where a delimiter "
                + "would occur so that the first message is as long as possible while falling "
                + "within the limit. The delimiter will be omitted at such boundaries. If you're "
                + "still confused about how this works, probably the best way to find out would be "
                + "to use numberlist to create a long string and then experiment with using this "
                + "function on that string.";
    }
    
    public String getName()
    {
        return "sendsplit";
    }
    
}
