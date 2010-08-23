package jw.jzbot.fact.functions.threading;

import jw.jzbot.ConfigVars;
import jw.jzbot.ConnectionWrapper;
import jw.jzbot.JZBot;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import jw.jzbot.fact.ast.FactEntity;
import jw.jzbot.fact.output.NullSink;

public class ThreadstartFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        // FIXME: maybe support for named threads? (which could then be checked to see if
        // they're alive, forcibly stopped, etc)
        final String threadName;
        final FactEntity code;
        String localVarRegex = "";
        if (arguments.length() == 1)
        {
            threadName = "";
            code = arguments.getEntity(0);
        }
        else if (arguments.length() == 2)
        {
            threadName = arguments.resolveString(0);
            code = arguments.getEntity(1);
        }
        else
        {
            threadName = arguments.resolveString(0);
            code = arguments.getEntity(1);
            localVarRegex = arguments.resolveString(2);
        }
        final FactContext newContext = context.cloneForThreading(localVarRegex);
        final String oldScope = context.currentScope();
        new Thread(
                "user-thread-"
                    + (threadName.equals("") ? "unnamed-" + System.currentTimeMillis()
                            : threadName))
        {
            public void run()
            {
                try
                {
                    code.resolve(new NullSink(), newContext);
                }
                catch (Throwable t)
                {
                    t.printStackTrace();
                    ConnectionWrapper usableConnection = null;
                    String usableChannel = null;
                    try
                    {
                        usableConnection =
                                JZBot.checkedGetExtractedConnection(oldScope, null);
                        usableChannel = JZBot.extractChannelName(oldScope);
                    }
                    catch (Exception e)
                    {
                        usableConnection =
                                JZBot.checkedGetExtractedConnection(ConfigVars.primary
                                        .get(), null);
                        usableChannel = JZBot.extractChannelName(ConfigVars.primary.get());
                    }
                    if (usableConnection != null && usableChannel != null)
                    {
                        usableConnection
                                .sendMessage(usableChannel, "The user thread " + threadName
                                    + " threw an exception: " + JZBot.pastebinStack(t));
                    }
                }
            }
        }.start();
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {threadstart|<name>|<code>|<regex>} -- THIS IS STILL REALLY "
            + "EXPERIMENTAL AND COULD LEAD TO CRASHES. Starts <code> running in "
            + "a separate thread. The output of this code is discarded. This "
            + "code will have its own local variable pool and scope level as "
            + "if it had been run in a separate factoid. Chain variables, "
            + "however, will be shared. All local variables matching <regex> "
            + "will be copied and the copies set as local variables in the scope "
            + "of the thread. The thread is allowed to continue running after "
            + "the factoid has completed, and will never time out. Syntax errors "
            + "or other exceptions that occur while running in the thread will "
            + "result in a pastebin being issued to the channel of the scope at "
            + "the time the thread was started. If there is no channel in that "
            + "scope, the error message will be dumped to the bot's primary "
            + "channel, or discarded if there is no primary channel. Right now, "
            + "the name is unused (except that it shows up in the output from "
            + "~status threads), but that could change in the future. <name> "
            + "and <regex> are optional, but the former is required if the "
            + "latter is present. They default to a generated name and the "
            + "empty string, respectively.";
    }
    
}
