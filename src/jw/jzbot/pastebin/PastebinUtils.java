package jw.jzbot.pastebin;

import java.io.PrintWriter;
import java.io.StringWriter;

import jw.jzbot.fact.exceptions.FactoidException;
import jw.jzbot.pastebin.PastebinProvider.Feature;
import jw.jzbot.utils.Pastebin;

public class PastebinUtils
{
    public static String pastebinNotice(String text, Feature[] features)
    {
        try
        {
            return Pastebin.createPost("jzbot", text
                + "\n", Pastebin.Duration.DAY, null, features);
        }
        catch (RuntimeException e)
        {
            System.out.println("Exception occurred while trying to "
                + "pastebin notice. The text to be sent to the pastebin was: \n\n" + text
                + "\n\n");
            throw e;
        }
    }
    
    public static String pastebinNotice(String text)
    {
        return pastebinNotice(text, null);
    }
    
    public static String pastebinStack(Throwable e)
    {
        e.printStackTrace();
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw, true));
        String eString = sw.toString();
        if (e instanceof FactoidException)
            eString =
                    ((FactoidException) e).createFactoidStackTrace()
                        + "\n\nJava stack trace:\n\n" + eString;
        try
        {
            return pastebinNotice(eString, null);
        }
        catch (Exception e2)
        {
            e2.printStackTrace();
            return "(pastebin service unavailable)";
        }
    }
    
    /**
     * Calls {@link #pastebinNotice(String, Feature[])}. If an exception is thrown,
     * <tt>alternative</tt> is returned, with "(pastebin unavailable) " prefixed.
     * 
     * @param text
     *            The text to pastebin
     * @param features
     *            The features that the target pastebin should have
     * @param alternative
     *            The text to return if the pastebin service is unavailable
     * @return the return value of pastebinNotice(String, Feature[]), or
     *         <tt>"(pastebin unavailable) " + alternative</tt> if the pastebin service is
     *         currently unavailable
     */
    public static String tryPastebin(String text, Feature[] features, String alternative)
    {
        try
        {
            return pastebinNotice(text, features);
        }
        catch (Exception e)
        {
            return "(pastebin unavailable) " + alternative;
        }
    }
    
}
