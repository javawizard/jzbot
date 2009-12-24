package jw.jzbot.fact.functions.li;

import java.util.Arrays;
import java.util.Collections;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.DelimitedSink;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

import net.sf.opengroove.common.utils.StringUtils;

public class RandomizeFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        String regex = arguments.resolveString(0);
        String string = arguments.resolveString(1);
        String delimiter = arguments.resolveString(2);
        String[] tokens = string.split(regex);
        Collections.shuffle(Arrays.asList(tokens));
        DelimitedSink result = new DelimitedSink(sink, delimiter);
        for (String s : tokens)
        {
            result.next();
            result.write(s);
        }
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{randomize||<regex>||<string>||<delimiter>}} -- Splits "
                + "<string> around the regular expression <regex>, shuffles the resulting "
                + "list of strings, and then evaluates to a <delimiter>-separated list "
                + "of those strings. For example, {{randomize||\\\\\\.||"
                + "first.second.third.fourth||-}} could evaluate to \"third-second-"
                + "first-fourth\" or maybe \"second-fourth-first-third\".";
    }
    
}
