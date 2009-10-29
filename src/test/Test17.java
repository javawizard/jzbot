package test;

import java.io.StringReader;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;
import org.opengroove.jzbot.utils.XMLUtils;

public class Test17
{
    public static void main(String[] args) throws Throwable
    {
        String xml = "<root f=\"bye\" g=\"hi\"><t d=\"first\"></t>hello<t d=\"second\">"
                + "</t><t d=\"third\"></t></root>";
        Document doc = new SAXBuilder().build(new StringReader(xml));
        Element e = (Element) XPath.selectSingleNode(doc, "/root/t[1]");
        Attribute a = (Attribute) XPath.selectSingleNode(e, "@d");
        System.out.println(e);
        System.out.println(a);
        String xe = XMLUtils.xpath(e);
        String xa = XMLUtils.xpath(a);
        System.out.println(xe);
        System.out.println(xa);
        System.out.println(XPath.selectSingleNode(doc, xe));
        System.out.println(XPath.selectSingleNode(doc, xa));
        System.out.println(XPath.selectNodes(doc, "/root/@*"));
    }
}
