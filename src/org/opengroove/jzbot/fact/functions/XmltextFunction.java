package org.opengroove.jzbot.fact.functions;

import java.util.List;

import net.sf.opengroove.common.utils.StringUtils;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;
import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.FactoidException;
import org.opengroove.jzbot.fact.Function;
import org.opengroove.jzbot.utils.XMLUtils;

public class XmltextFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        try
        {
            Document doc = context.getXmlDocuments().get(arguments.get(0));
            String path = arguments.get(1);
            Object o = XPath.selectSingleNode(doc.getRootElement(), path);
            if (o instanceof Element)
                return ((Element) o).getText();
            else
                return ((Attribute) o).getValue();
        }
        catch (Exception e)
        {
            throw new FactoidException(e);
        }
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{xmltext||<name>||<path>}} -- Finds the element or attribute "
                + "within the document named by <name> that matches <path>, and evaluates "
                + "to its text contents if it's an element or its attribute value if it's"
                + " an attribute.";
    }
    
}
