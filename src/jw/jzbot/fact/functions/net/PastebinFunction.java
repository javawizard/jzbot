package jw.jzbot.fact.functions.net;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.FactoidException;
import jw.jzbot.fact.Function;
import jw.jzbot.pastebin.PastebinProvider.Feature;
import jw.jzbot.utils.Pastebin;
import jw.jzbot.utils.Pastebin.Duration;

public class PastebinFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        if (arguments.length() > 1 && arguments.get(1).toLowerCase().contains("forever"))
            throw new FactoidException("Forever pastebins are not allowed at this time.");
        context.incrementMessageCount();
        Duration duration = (arguments.length() > 1 ? Duration.valueOf(arguments.get(1))
                : Duration.DAY);
        Feature[] features;
        if (duration == Duration.FOREVER)
            features = new Feature[]
            {
                Feature.forever
            };
        else
            features = new Feature[0];
        return Pastebin.createPost("jzbot-fact", arguments.get(0), duration, null, null);
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
