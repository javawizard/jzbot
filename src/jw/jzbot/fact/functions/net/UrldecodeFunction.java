package jw.jzbot.fact.functions.net;

import java.net.URLDecoder;
import java.net.URLEncoder;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import jw.jzbot.fact.exceptions.FactoidException;

public class UrldecodeFunction extends Function
{
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        try
        {
            sink.write(URLDecoder.decode(arguments.resolveString(0), "UTF-8"));
        }
        catch (Exception e)
        {
            throw new FactoidException("Exception while decoding URL fragment "
                + arguments.getString(0), e);
        }
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {urldecode|<text>} -- Decodes the url-encoded text specified.";
    }
    
}
