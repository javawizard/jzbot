package jw.jzbot.fact.functions.net;

import java.net.URLEncoder;

import jw.jzbot.ConfigVars;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import jw.jzbot.fact.exceptions.FactoidException;

public class UrlencodeFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        try
        {
            sink.write(URLEncoder.encode(arguments.getString(0), ConfigVars.charset.get()));
        }
        catch (Exception e)
        {
            throw new FactoidException("Exception while encoding URL fragment "
                    + arguments.getString(0), e);
        }
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {urlencode|<text>} -- Encodes the text specified so that it is "
                + "suitable for including in a URL.";
    }
    
    public String getName()
    {
        return "urlencode";
    }
    
}
