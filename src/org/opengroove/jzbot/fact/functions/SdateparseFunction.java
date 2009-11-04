package org.opengroove.jzbot.fact.functions;

import java.text.SimpleDateFormat;

import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.FactoidException;
import org.opengroove.jzbot.fact.Function;

public class SdateparseFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        SimpleDateFormat format = new SimpleDateFormat(arguments.get(0));
        try
        {
            return "" + format.parse(arguments.get(1)).getTime();
        }
        catch (Exception e)
        {
            throw new FactoidException("Exception occured while parsing date \""
                    + arguments.get(1) + "\" with format \"" + arguments.get(0) + "\"", e);
        }
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{sdateparse||<format>||<text>}} -- Parses <text>, which is a date "
                + "in the format specified by <format> (which should be a Java "
                + "SimpleDateFormat string), into a number representing the parsed date."
                + "See http://java.sun.com/javase/6/docs/api/ for information on the "
                + "syntax of <format>.";
    }
    
}
