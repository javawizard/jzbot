package org.opengroove.jzbot.fact.functions;

import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.Function;

public class JavadocFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public String getHelp(String topic)
    {
        if (topic == null || "".equals(topic))
            return "Syntax: {{javadoc||<action>||<desc>}} -- Performs a javadoc-related "
                    + "action on a class or method descriptor <desc>. <desc> can be one "
                    + "of \"<package>.<class>\", \"<class>\" (which will only work if "
                    + "there is only one class named <class> in the javadoc tree that "
                    + "this command searches), \"<package>.<class>.<method>\", or "
                    + "\"<class>.<method>\". <method> is the name of a method, with\n"
                    + "argument types enclosed with parentheses at the end. All "
                    + "subpages of this page are different actions that you can use "
                    + "for <action>, and each subpage describes what that action does.";
        return "{{javadoc}} doesn't have information about that topic yet.";
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
