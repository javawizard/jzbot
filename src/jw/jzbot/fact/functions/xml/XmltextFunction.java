package jw.jzbot.fact.functions.xml;

import java.util.List;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import jw.jzbot.fact.exceptions.FactoidException;
import jw.jzbot.utils.XMLUtils;

import net.sf.opengroove.common.utils.StringUtils;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

public class XmltextFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        try
        {
            Document doc = context.getXmlDocuments().get(arguments.getString(0));
            if (doc == null)
                throw new FactoidException("No document by the name "
                        + arguments.getString(0));
            String path = arguments.resolveString(1);
            Object o = XPath.selectSingleNode(doc.getRootElement(), path);
            if (o == null)
                throw new FactoidException("No xpath match for statement \"" + path
                        + "\", document is " + doc.toString());
            if (o instanceof Element)
                sink.write(((Element) o).getText());
            else
                sink.write(((Attribute) o).getValue());
        }
        catch (Exception e)
        {
            throw new FactoidException(e);
        }
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {xmltext|<name>|<path>} -- Finds the element or attribute "
                + "within the document named by <name> that matches <path>, and evaluates "
                + "to its text contents if it's an element or its attribute value if it's"
                + " an attribute.";
    }
    
}
