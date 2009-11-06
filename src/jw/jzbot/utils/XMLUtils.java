package jw.jzbot.utils;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;

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
}
