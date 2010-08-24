package jw.jzbot.fact.functions.factpack;

import jw.jzbot.JZBot;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import jw.jzbot.storage.StorageContainer;

public class FphasdepFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        /*
         * Because this function is called from {fpcheckdep}, we need to use
         * arguments.get...() instead of arguments.resolve...() to ensure that the value
         * is cached in case {fpcheckdep} needs it.
         */
        String target;
        String factpack;
        if (arguments.length() == 1)
        {
            target = context.getLocalVars().get("factpack-target");
            factpack = arguments.getString(0);
        }
        else
        {
            target = arguments.getString(0);
            factpack = arguments.getString(1);
        }
        String server = JZBot.extractRelativeServer(target, null);
        String channel = JZBot.extractRelativeChannel(target, null);
        StorageContainer container;
        if (server == null)
            container = JZBot.storage;
        else if (channel == null)
            container = JZBot.storage.getServer(server);
        else
            container = JZBot.storage.getServer(server).getChannel(channel);
        if (container == null)
            // TODO: consider throwing an exception here instead of just returning
            sink.write("0");
        else if (container.getFactpackFactoids(factpack).length > 0)
            sink.write("1");
        else
            sink.write("0");
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {fphasdep|<target>|<factpack>} -- Evaluates to 1 if there "
                + "are any factoids on the scope <target> that were installed by a "
                + "factpack named <factpack>. <target> is optional, and if it is "
                + "not present it will be taken from the local variable "
                + "\"factpack-target\".";
    }
    
}
