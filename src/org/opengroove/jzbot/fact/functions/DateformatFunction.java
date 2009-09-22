package org.opengroove.jzbot.fact.functions;

import java.util.Date;

import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.Function;

public class DateformatFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        return new Date(Long.parseLong(arguments.get(0))).toString();
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{dateformat||<value>}} -- Formats <value>, which should be "
                + "a number of milliseconds since Midnight January 1, 1970 UTC, as "
                + "a human-readable date string. If you want a custom date format "
                + "instead of the default one that this function provides, consider "
                + "using {{format}} with a custom format string instead "
                + "of {{dateformat}}.";
    }
    
    public String getName()
    {
        return "dateformat";
    }
    
}
