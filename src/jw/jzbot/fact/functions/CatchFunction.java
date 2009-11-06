package jw.jzbot.fact.functions;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;

public class CatchFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        try
        {
            return arguments.get(0);
        }
        catch (Exception e)
        {
            String prefix = arguments.get(1);
            Throwable root = e;
            while (root.getCause() != null)
            {
                root = root.getCause();
            }
            context.getLocalVars().put(prefix + "-class",
                    e.getClass().getName());
            context.getLocalVars().put(prefix + "-message", e.getMessage());
            context.getLocalVars().put(prefix + "-root-class",
                    root.getClass().getName());
            context.getLocalVars().put(prefix + "-root-message",
                    root.getMessage());
            return arguments.get(2);
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
