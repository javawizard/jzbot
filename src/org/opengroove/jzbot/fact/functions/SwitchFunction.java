package org.opengroove.jzbot.fact.functions;

import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.Function;

public class SwitchFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        String value = arguments.get(0);
        arguments = arguments.subList(1);
        int cases = arguments.length() / 2;
        System.out.println("" + cases + " cases in {{switch}}");
        for (int i = 0; i < cases; i++)
        {
            if (value.equals(arguments.get(i * 2)))
                return arguments.get((i * 2) + 1);
        }
        if ((arguments.length() % 2) == 1)
            // This means there's an odd number of arguments, not including
            // <value>, which means that a default was specified, so we'll
            // return it.
            return arguments.get(arguments.length() - 1);
        // This means there's an even number of arguments, not including
        // <value>, so there wasn't a default. Since there's not a default,
        // we'll return the empty string.
        return "";
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{switch||<value>||<test1>||<case1>||<test2>||<case2>||...||<default>}}"
                + " -- If <value> is equal (this is case-sensitive) to <test1>, then this "
                + "evaluates to <case1>. If not, then if <value> is equal to <test2>, then "
                + "this evaluates to <case2>, and so on. If <value> isn't equal to any of "
                + "the <test> arguments, then the switch function evaluates to <default>.\n"
                + "<default> is entirely optional. If it's not present, and none of the "
                + "<test> arguments match <value>, then the switch function will evaluate "
                + "to nothing. For example, {{switch||2||1||first||2||second||3||third||missing}} "
                + "would evaluate to \"second\", and {{switch||5||1||first||2||second||"
                + "3||third||missing}} would evaluate to \"missing\".";
    }
    
}
