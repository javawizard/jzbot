package org.opengroove.jzbot.fact.functions;

import java.util.List;

import net.sf.opengroove.common.utils.StringUtils;

import org.jdom.Document;
import org.jdom.xpath.XPath;
import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.FactoidException;
import org.opengroove.jzbot.fact.Function;
import org.opengroove.jzbot.utils.XMLUtils;

public class XmlsearchFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        try
        {
            Document doc = context.getXmlDocuments().get(arguments.get(0));
            String path = arguments.get(1);
            List list = XPath.selectNodes(doc.getRootElement(), path);
            String[] strings = new String[list.size()];
            for (int i = 0; i < strings.length; i++)
            {
                strings[i] = XMLUtils.xpath(list.get(i));
            }
            return StringUtils.delimited(strings, "\n");
        }
        catch (Exception e)
        {
            throw new FactoidException(e);
        }
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{xmlsearch||<name>||<path>}} -- Searches the XML document named "
                + "<name> for any elements or attributes that match the XPath <path>, "
                + "and evaluates to a newline-separated list of unambiguous XPaths that "
                + "represent each matching item. The search is performed with the root "
                + "element of the doucument as the context.";
    }
    
}
