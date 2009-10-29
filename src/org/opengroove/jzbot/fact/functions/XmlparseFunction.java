package org.opengroove.jzbot.fact.functions;

import java.io.StringReader;

import org.jdom.input.SAXBuilder;
import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.FactoidException;
import org.opengroove.jzbot.fact.Function;

public class XmlparseFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        try
        {
            context.getXmlDocuments().put(arguments.get(0),
                    new SAXBuilder().build(new StringReader(arguments.get(1))));
        }
        catch (Exception e)
        {
            throw new FactoidException(e);
        }
        return "";
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{xmlparse||<name>||<text>}} -- Parses <text>, which should be "
                + "a valid XML document, and stores it as a document called <name>. "
                + "Within the local scope of this factoid, various {{xml<something>}} "
                + "functions can be called to get access to stuff within this "
                + "XML document.";
    }
    
}
