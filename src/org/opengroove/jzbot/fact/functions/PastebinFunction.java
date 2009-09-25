package org.opengroove.jzbot.fact.functions;

import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.FactoidException;
import org.opengroove.jzbot.fact.Function;
import org.opengroove.jzbot.utils.Pastebin;
import org.opengroove.jzbot.utils.Pastebin.Duration;

public class PastebinFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        if (arguments.length() > 1
                && arguments.get(1).toLowerCase().contains("forever"))
            throw new FactoidException(
                    "Forever pastebins are not allowed at this time.");
        context.incrementMessageCount();
        return "http://pastebin.com/"
                + Pastebin.createPost("jzbot-fact", arguments.get(0),
                        (arguments.length() > 1 ? Duration.valueOf(arguments
                                .get(1)) : Duration.DAY), null);
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{pastebin||<text>||<duration>}} -- Creates a post at http://pastebin.com "
                + "with the specified text and the specified duration (which should be either "
                + "\"day\" or \"month\"). <duration> is optional, and will default to \"day\" if "
                + "not present.";
    }
    
}
