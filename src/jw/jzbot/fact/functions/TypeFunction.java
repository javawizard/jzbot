package jw.jzbot.fact.functions;

import jw.jzbot.JZBot;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.FactoidException;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import jw.jzbot.storage.Channel;

public class TypeFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        String factname = arguments.resolveString(0);
        boolean isGlobal = JZBot.storage.getFactoid(factname) != null;
        boolean isChannel = false;
        if (context.getChannel() != null)
        {
            Channel c = context.checkedGetDatastoreServer()
                    .getChannel(context.getChannel());
            if (c != null)
            {
                if (c.getFactoid(factname) != null)
                    isChannel = true;
            }
        }
        if (isGlobal && isChannel)
            sink.write("both");
        else if (isGlobal && !isChannel)
            sink.write("global");
        else if (isChannel && !isGlobal)
            sink.write("channel");
        else
            sink.write("none");
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{type||<factoidname>}} -- Evaluates to the type of factoid that "
                + "<factoidname> is. This is either \"global\", \"channel\", or \"none\". "
                + "Global means that the factoid is a global factoid, channel means that the "
                + "factoid is a channel-specific factoid, and nonexistent means that the "
                + "factoid in question does not exist. This checks for channel-specific "
                + "factoids only on the current channel.\n"
                + "If there is both a channel-specific factoid and a global factoid with "
                + "the name <factname>, \"both\" will be returned.";
    }
    
}
