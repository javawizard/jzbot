package jw.jzbot.utils;

import java.io.StringReader;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

public class XMLUtils
{
    public static String xpath(Element element)
    {
        Document doc = element.getDocument();
        Element root = doc.getRootElement();
        /*
         * To construct the path, we're going to recurse. If the element is not the
         * document root element, then our path is the element's parent's path plus this
         * one's immediate path.
         */
        String parentPath = "";
        if (!element.equals(root))
            parentPath = xpath(element.getParentElement());
        /*
         * Now we construct our immediate path. For now, this is simply an asterisk, an
         * open bracket, and the index of this element within its children.
         */
        int indexInParent;
        if (element.equals(root))
            indexInParent = 1;
        else
            indexInParent = 1 + element.getParentElement().getChildren().indexOf(element);
        String immediatePath = "/*[" + indexInParent + "]";
        /*
         * Now we combine the two, and return the result.
         */
        return parentPath + immediatePath;
    }
    
    public static String xpath(Attribute attribute)
    {
        return xpath(attribute.getParent()) + "/@" + attribute.getName();
    }
    
    public static String xpath(Object o)
    {
        if (o instanceof Element)
        {
            return xpath((Element) o);
        }
        else
        {
            return xpath((Attribute) o);
        }
    }
    
    /**
     * Parses the specified XML text into an XML document, then returns the text of the
     * element named by the specified XPath string. This is semantically equivalent to the
     * Fact program <tt>{xmlparse|doc|&lt;xml>}{xmltext|doc|&lt;xpath>}</tt>, where
     * <tt>&lt;xml></tt> and <tt>&lt;xpath></tt> are their respective parameters passed to
     * this method.<br/><br/>
     * 
     * If there is no such element or attribute, this method will return <tt>null</tt>
     * .<br/><br/>
     * 
     * If the XPath expression refers to an object that is not an element or an attribute,
     * a {@link ClassCastException} will be thrown. I might raise this restriction later
     * to add support for other element types. Patches to add support for other types
     * would be welcome.
     * 
     * @param xml
     *            The XML text to parse
     * @param xpath
     *            The XPath path to search for
     * @return The text of the element denoted by the specified XPath string or the value
     *         of the attribute denoted by the specified XPath string
     */
    public static String getXpathText(String xml, String xpath)
    {
        try
        {
            Document doc = new SAXBuilder().build(new StringReader(xml));
            Object result = XPath.selectSingleNode(doc.getRootElement(), xpath);
            if (result == null)
                return null;
            if (result instanceof Element)
                return ((Element) result).getText();
            else
                return ((Attribute) result).getValue();
        }
        catch (Exception e)
        {
            throw new RuntimeException("Exception while searching for path \"" + xpath
                + "\" in xml \"" + xml + "\"", e);
        }
    }
}
