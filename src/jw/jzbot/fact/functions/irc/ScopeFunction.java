package jw.jzbot.fact.functions.irc;

import jw.jzbot.JZBot;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.FactoidException;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class ScopeFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        String channel = context.getChannel();
        String server = context.getServer();
        String oldChannel = channel;
        String oldServer = server;
        String newScope = arguments.resolveString(0);
        if (newScope.equals(""))
        {
            channel = null;
            server = null;
        }
        else if (newScope.startsWith("#") && !newScope.contains("@"))
        {
            if (server == null)
                throw new FactoidException("The new scope \"" + newScope
                        + "\" contains only a channel, but the current scope "
                        + "doesn't contain a server. You need to include a "
                        + "server to scope to.");
            channel = newScope;
        }
        else if (newScope.startsWith("@") && !newScope.contains("#"))
        {
            server = newScope.substring(1);
            channel = null;
        }
        else if(newScope.startsWith("@") && newScope.contains("#"))
        {
            server = JZBot.extractServerName(newScope);
            channel = JZBot.extractChannelName(newScope);
        }
        context.setChannel(channel);
        context.setServer(server);
        arguments.resolve(1, sink);
        context.setChannel(oldChannel);
        context.setServer(oldServer);
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{scope||<newscope>||<action>}} -- Runs <action> (and evaluates "
                + "to it), but with the invocation context switched to the specified scope. "
                + "A scope is specified as either a server prefixed with \"@\", a channel "
                + "that starts with \"#\", both together (with the server first), or the "
                + "empty string. This does not change any scope-related local variables.";
    }
    
}
