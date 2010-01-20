package jw.jzbot.fact.functions.xml;

import java.util.List;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import jw.jzbot.fact.exceptions.FactoidException;
import jw.jzbot.utils.XMLUtils;

import net.sf.opengroove.common.utils.StringUtils;

import org.jdom.Document;
import org.jdom.xpath.XPath;

public class XmlsearchFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        try
        {
            Document doc = context.getXmlDocuments().get(arguments.resolveString(0));
            String path = arguments.resolveString(1);
            List list = XPath.selectNodes(doc.getRootElement(), path);
            String[] strings = new String[list.size()];
            for (int i = 0; i < strings.length; i++)
            {
                strings[i] = XMLUtils.xpath(list.get(i));
            }
            // TODO: change this to use a DelimitedSink
            sink.write(StringUtils.delimited(strings, "\n"));
        }
        catch (Exception e)
        {
            throw new FactoidException(e);
        }
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {xmlsearch|<name>|<path>} -- Searches the XML document named "
                + "<name> for any elements or attributes that match the XPath <path>, "
                + "and evaluates to a newline-separated list of unambiguous XPaths that "
                + "represent each matching item. The search is performed with the root "
                + "element of the doucument as the context.";
    }
    
}
