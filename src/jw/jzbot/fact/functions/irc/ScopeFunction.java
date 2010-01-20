package jw.jzbot.fact.functions.irc;

import jw.jzbot.JZBot;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import jw.jzbot.fact.exceptions.FactoidException;

public class ScopeFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        if (arguments.length() == 0)
        // we're supposed to return the current scope
        {
            sink.write(context.currentScope());
        }
        else
        {
            String newScope = arguments.resolveString(0);
            String oldServer = context.getServer();
            String oldChannel = context.getChannel();
            String newServer;
            String newChannel;
            if (newScope.equals(""))
            {
                newServer = null;
                newChannel = null;
            }
            else
            {
                newServer = JZBot.extractRelativeServer(newScope, context);
                newChannel = JZBot.extractRelativeChannel(newScope, context);
            }
            if (newServer == null && newChannel != null)
                throw new FactoidException("Using the new scope " + newScope
                        + " in the context of the server " + oldServer
                        + " and the channel " + oldChannel
                        + " would result in a scope that contains a "
                        + "channel but not a server. This is not allowed.");
            context.setServer(newServer);
            context.setChannel(newChannel);
            /*
             * If the user just wanted to set the scope, we're done. If, however, they
             * wanted to run an action, we need to run it and set the scope back to what
             * it used to be.
             */
            if (arguments.length() == 2)
            {
                arguments.resolve(1, sink);
                context.setServer(oldServer);
                context.setChannel(oldChannel);
            }
        }
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {scope}, {scope|<newscope>}, or {scope|<newscope>|<action>}"
                + " -- The first version evaluates to the fully-qualified current "
                + "scope. The second form sets the new scope to be used for the "
                + "remainder of this factoid; this does not, however, persist to "
                + "a factoid that might have imported this factoid. The third form\n"
                + "runs <action> with the specified scope, and then sets the scope "
                + "back to what it was after <action> has been run.";
    }
    
}
