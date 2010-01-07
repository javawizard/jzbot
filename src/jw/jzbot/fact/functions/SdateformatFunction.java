package jw.jzbot.fact.functions;

import java.text.SimpleDateFormat;
import java.util.Date;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.FactoidException;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class SdateformatFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        SimpleDateFormat format = new SimpleDateFormat(arguments.getString(0));
        try
        {
            sink.write(format.format(new Date(Long.parseLong(arguments.getString(1)))));
        }
        catch (Exception e)
        {
            throw new FactoidException("Exception occured while formatting date \""
                    + arguments.getString(1) + "\" with format \"" + arguments.getString(0) + "\"", e);
        }
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {sdateformat|<format>|<date>} -- Formats the date <date>, which "
                + "is a number of milliseconds since January 1, 1970, using the Java "
                + "SimpleDateFormat string <format>. See "
                + "http://java.sun.com/javase/6/docs/api/ for information on the syntax "
                + "of <format>.";
    }
    
}
