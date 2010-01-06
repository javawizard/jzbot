package jw.jzbot.fact.functions;

import java.util.ArrayList;
import java.util.Collections;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

import net.sf.opengroove.common.utils.StringUtils;

public class IntervalFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        ArrayList<String> strings = new ArrayList<String>();
        long duration = Long.parseLong(arguments.resolveString(0));
        add(strings, "second", duration % 60);
        duration /= 60;
        add(strings, "minute", duration % 60);
        duration /= 60;
        add(strings, "hour", duration % 24);
        duration /= 24;
        add(strings, "day", duration % 365);
        duration /= 365;
        add(strings, "year", duration);
        if (strings.size() == 0)
        {
            sink.write("0 seconds");
            return;
        }
        Collections.reverse(strings);
        sink.write(StringUtils.delimited(strings.toArray(new String[0]), " "));
    }
    
    private void add(ArrayList<String> strings, String unit, long duration)
    {
        if (duration != 0)
            strings.add("" + duration + " " + unit + (duration == 1 ? "" : "s"));
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {interval|<seconds>} -- Formats the specified number of seconds "
                + "as an interval. For example, {interval|137} would evaluate to "
                + "\"2 minutes 17 seconds\". This function supports all the way up to "
                + "years (meaning {interval|31536000} evaluates to \"1 year\").";
    }
    
}
