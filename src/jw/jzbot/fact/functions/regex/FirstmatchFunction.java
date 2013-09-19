package jw.jzbot.fact.functions.regex;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class FirstmatchFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        String regex = arguments.resolveString(0);
        for (int i = 1; i < (arguments.length() - 1); i++)
        {
            String test = arguments.resolveString(i);
            if (test.matches(regex))
            {
                sink.write(test);
                return;
            }
        }
        sink.write(arguments.resolveString(arguments.length() - 1));
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {firstmatch|<regex>|<v1>|<v2>|...|<default>} -- "
            + "Evaluates to <v1> if it matches <regex>. If it doesn't, evaluates "
            + "to <v2> if it matches, and so on. If none of them match, "
            + "evaluates to <default>.";
    }
    
}
