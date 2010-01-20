package jw.jzbot.fact.functions;

import java.io.File;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import jw.jzbot.fact.output.DelimitedSink;

import net.sf.opengroove.common.utils.StringUtils;

public class ListresourcesFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        DelimitedSink result = new DelimitedSink(sink, "/");
        for (String s : new File("resources").list())
        {
            result.next();
            result.write(s);
        }
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {listresources} -- Returns a forward-slash-separated list of resources "
                + "available to the bot. See {getresource} for information on what a resource is.";
    }
    
}
