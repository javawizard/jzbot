package jw.jzbot.fact.functions.li;

import java.util.ArrayList;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class RandomsplitFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        String[] strings = arguments.resolveString(0).split(arguments.resolveString(1));
        if (arguments.length() > 2)
        {
            String regex = arguments.resolveString(2);
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
        if (strings.length != 0)
            sink.write(strings[(int) (Math.random() * strings.length)]);
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {randomsplit|<text>|<delimiter>|<regex>} -- Splits <text> around "
                + "the delimiter <delimiter> (which is a regular expression), and then chooses "
                + "one of the resulting strings at random and evaluates to that string. If <regex> "
                + "is present (<regex> is entirely optional), then only strings that exactly "
                + "match <regex> will be considered for random choosing.";
    }
    
}
