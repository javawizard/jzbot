package jw.jzbot.fact.functions;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import jw.jzbot.fact.StringSink;

public class CatchFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        try
        {
            /*
             * We want to do this in a string sink so that if it throws an exception, we
             * won't get half-written results sent to the main sink.
             */
            StringSink ss = new StringSink();
            arguments.resolve(0, ss);
            sink.write(ss.toString());
        }
        catch (Exception e)
        {
            String prefix = arguments.getString(1);
            Throwable root = e;
            while (root.getCause() != null)
            {
                root = root.getCause();
            }
            context.getLocalVars().put(prefix + "-class", e.getClass().getName());
            context.getLocalVars().put(prefix + "-message", e.getMessage());
            context.getLocalVars().put(prefix + "-root-class", root.getClass().getName());
            context.getLocalVars().put(prefix + "-root-message", root.getMessage());
            arguments.resolve(2, sink);
        }
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{catch||<action>||<prefix>||<onerror>}} -- Evaluates to <action>. "
                + "If <action> ends up throwing an exception, <onerror> is run instead. "
                + "A few local variables are set if an error does occur. Their names "
                + "are <prefix>-class, which is the name of the class of the error (this "
                + "is usually jw.jzbot.fact.FactoidException), <prefix>-message, "
                + "\nwhich is the error message of the exception, and <prefix>-root-class "
                + "and <prefix>-root-message, which are the class and message of "
                + "the root cause of the exception. This function will catch all errors "
                + "(including too-many-message and too-many-import errors) except "
                + "an error indicating that the factoid used up too much time, for the "
                + "\nobvious reason that this could lead to factoids that never stop running. "
                + "This will not allow for circumvention of the message limit, however, as "
                + "each successive invocation of {{sendmessage}} would just throw another "
                + "error instead of sending a message if the quota has been exceeded.";
    }
    
}
