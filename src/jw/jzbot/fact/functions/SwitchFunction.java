package jw.jzbot.fact.functions;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class SwitchFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        String value = arguments.resolveString(0);
        arguments = arguments.subList(1);
        int cases = arguments.length() / 2;
        System.out.println("" + cases + " cases in {switch}");
        for (int i = 0; i < cases; i++)
        {
            if (value.equals(arguments.resolveString(i * 2)))
            {
                sink.write(arguments.resolveString((i * 2) + 1));
                return;
            }
        }
        if ((arguments.length() % 2) == 1)
            // This means there's an odd number of arguments, not including
            // <value>, which means that a default was specified, so we'll
            // return it. If there wasn't a default, then we won't do anything.
            sink.write(arguments.resolveString(arguments.length() - 1));
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {switch|<value>|<test1>|<case1>|<test2>|<case2>|...|<default>}"
                + " -- If <value> is equal (this is case-sensitive) to <test1>, then this "
                + "evaluates to <case1>. If not, then if <value> is equal to <test2>, then "
                + "this evaluates to <case2>, and so on. If <value> isn't equal to any of "
                + "the <test> arguments, then the switch function evaluates to <default>.\n"
                + "<default> is entirely optional. If it's not present, and none of the "
                + "<test> arguments match <value>, then the switch function will evaluate "
                + "to nothing. For example, {switch|2|1|first|2|second|3|third|missing} "
                + "would evaluate to \"second\", and {switch|5|1|first|2|second|"
                + "3|third|missing} would evaluate to \"missing\".";
    }
    
}
