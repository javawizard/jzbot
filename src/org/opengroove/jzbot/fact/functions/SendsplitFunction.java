package org.opengroove.jzbot.fact.functions;

import org.opengroove.jzbot.JZBot;
import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.Function;
import org.opengroove.jzbot.utils.JZUtils;

public class SendsplitFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        String[] tokens = JZUtils.ircDelimited(arguments.get(3).split(
                arguments.get(1)), arguments.get(2));
        for (String s : tokens)
        {
            context.incrementMessageCount();
            JZBot.bot.sendMessage(arguments.get(0), s);
            try
            {
                Thread.sleep(500);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        return "";
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{sendsplit||<to>||<regex>||<delimiter>||<message>}} -- Divides <message> "
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
    
    @Override
    public String getName()
    {
        return "sendsplit";
    }
    
}
