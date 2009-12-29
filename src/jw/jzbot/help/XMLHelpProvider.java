package jw.jzbot.help;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jw.jzbot.HelpProvider;
import jw.jzbot.JZBot;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

public class XMLHelpProvider implements HelpProvider
{
    private class HelpPage
    {
        private String text;
        private Map<String, HelpPage> pages = new HashMap<String, HelpPage>();
    }
    
    private HelpPage mainPage;
    
    public XMLHelpProvider(InputStream in)
    {
        init(in);
    }
    
    private void init(InputStream in)
    {
        try
        {
            Document doc = new SAXBuilder().build(in);
            mainPage = parseHelp(doc.getRootElement());
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    public XMLHelpProvider(String file)
    {
        try
        {
            init(new FileInputStream(file));
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    private HelpPage parseHelp(Element element)
    {
        HelpPage page = new HelpPage();
        Element[] helpTextElements = (Element[]) element.getChildren("helptext").toArray(
                new Element[0]);
        StringBuffer buffer = new StringBuffer();
        for (Element e : helpTextElements)
        {
            if (buffer.length() > 0)
                buffer.append("\n");
            buffer.append(e.getText().trim().replace("\r", "").replaceAll(" *\n *", " ")
                    .replaceAll("^ *", "").replaceAll(" *$", ""));
        }
        page.text = buffer.toString();
        List subElements = element.getChildren();
        for (Object o : subElements)
        {
            if ((o instanceof Element) && !((Element) o).getName().equals("helptext"))
            {
                Element subElement = (Element) o;
                page.pages.put(subElement.getName(), parseHelp(subElement));
            }
        }
        return page;
    }
    
    @Override
    public String getPage(String page)
    {
        HelpPage helpPage = getPageForName(page);
        if (helpPage == null)
            return null;
        return helpPage.text/* .replace("%SELFNICK%", JZBot.bot.getNick()) */;
    }
    
    @Override
    public String[] listPages(String page)
    {
        HelpPage helpPage = getPageForName(page);
        if (helpPage == null)
            return new String[0];
        return helpPage.pages.keySet().toArray(new String[0]);
    }
    
    private HelpPage getPageForName(String name)
    {
        if (name.equals(""))
            return mainPage;
        String[] tokens = name.split(" ");
        HelpPage current = mainPage;
        for (String s : tokens)
        {
            current = current.pages.get(s);
            if (current == null)
                return null;
        }
        return current;
    }
    
}
