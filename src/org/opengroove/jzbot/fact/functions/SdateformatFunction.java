package org.opengroove.jzbot.fact.functions;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.FactoidException;
import org.opengroove.jzbot.fact.Function;

public class SdateformatFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        SimpleDateFormat format = new SimpleDateFormat(arguments.get(0));
        try
        {
            return "" + format.format(new Date(Long.parseLong(arguments.get(1))));
        }
        catch (Exception e)
        {
            throw new FactoidException("Exception occured while formatting date \""
                    + arguments.get(1) + "\" with format \"" + arguments.get(0) + "\"", e);
        }
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{sdateformat||<format>||<date>}} -- Formats the date <date>, which "
                + "is a number of milliseconds since January 1, 1970, using the Java "
                + "SimpleDateFormat string <format>. See "
                + "http://java.sun.com/javase/6/docs/api/ for information on the syntax "
                + "of <format>.";
    }
    
}
