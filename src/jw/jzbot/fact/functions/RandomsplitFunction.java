package jw.jzbot.fact.functions;

import java.util.ArrayList;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;


public class RandomsplitFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        String[] strings = arguments.get(0).split(arguments.get(1));
        if (arguments.length() > 2)
        {
            String regex = arguments.get(2);
            System.out.println("regex:");
            ArrayList<String> newStrings = new ArrayList<String>();
            for (String s : strings)
            {
                boolean matches = s.matches(regex);
                System.out.println("matches \"" + s + "\"? " + matches);
                if (matches)
                    newStrings.add(s);
            }
            strings = newStrings.toArray(new String[0]);
        }
        System.out.println("" + strings.length + " strings");
        if (strings.length == 0)
            return "";
        return strings[(int) (Math.random() * strings.length)];
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{randomsplit||<text>||<delimiter>||<regex>}} -- Splits <text> around "
                + "the delimiter <delimiter> (which is a regular expression), and then chooses "
                + "one of the resulting strings at random and evaluates to that string. If <regex> "
                + "is present (<regex> is entirely optional), then only strings that exactly "
                + "match <regex> will be considered for random choosing.";
    }
    
}
