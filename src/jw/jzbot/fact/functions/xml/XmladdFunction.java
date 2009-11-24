package jw.jzbot.fact.functions.xml;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class XmladdFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{xmladd||<target>||<index>||<tagname>}} -- Adds an XML tag, with "
                + "the tag name <tagname>, to the target element <target> at the position "
                + "<index>. 0 is the first element. This then evaluates to ";
    }
    
}
