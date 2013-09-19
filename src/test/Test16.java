package test;

import java.io.StringReader;
import java.util.List;

import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

public class Test16
{
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Throwable
    {
        String xml = "<root f=\"bye\" g=\"hi\"><t d=\"first\"></t>hello<t d=\"second\">"
                + "</t><t d=\"third\"></t></root>";
        Document doc = new SAXBuilder().build(new StringReader(xml));
        List list = XPath.selectNodes(doc.getRootElement(), "/*[1]/*[3]/@d");
        System.out.println(list);
    }
    
}
