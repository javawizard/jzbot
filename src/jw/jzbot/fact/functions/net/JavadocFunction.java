package jw.jzbot.fact.functions.net;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class JavadocFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public String getHelp(String topic)
    {
        if (topic == null || "".equals(topic))
            return "Syntax: {javadoc|<action>|<desc>} -- Performs a javadoc-related "
                    + "action on a class or method descriptor <desc>. <desc> can be one "
                    + "of \"<package>.<class>\", \"<class>\" (which will only work if "
                    + "there is only one class named <class> in the javadoc tree that "
                    + "this command searches), \"<package>.<class>.<method>\", or "
                    + "\"<class>.<method>\". <method> is the name of a method, with\n"
                    + "argument types enclosed with parentheses at the end. All "
                    + "subpages of this page are different actions that you can use "
                    + "for <action>, and each subpage describes what that action does.";
        return "{javadoc} doesn't have information about that topic yet.";
    }
    
    @Override
    public String[] getTopics()
    {
        return new String[]
        {
                "resolve", "url", "methods", "classes"
        };
    }
    
}
