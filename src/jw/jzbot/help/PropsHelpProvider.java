package jw.jzbot.help;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import jw.jzbot.HelpProvider;
import jw.jzbot.JZBot;

public class PropsHelpProvider implements HelpProvider
{
    private HashMap<String, String> props = new HashMap<String, String>();
    
    public PropsHelpProvider(String filename)
    {
        try
        {
            Properties props = new Properties();
            props.load(new FileInputStream(filename));
            /*
             * The reason we're creating an extra variable, propsGeneric, here is that
             * Properties extends Hashtable<Object,Object>, so it's not directly
             * type-compatible with Map<String,String>. We know, however, that, being a
             * properties object, props won't have any non-string values in it, so we'll
             * use the propsGeneric variable to circumvent Java's generic type checking
             * mechanism.
             */
            Map propsGeneric = props;
            this.props.putAll(propsGeneric);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public String getPage(String page)
    {
        String s = props.get(page.replace(".", "_").replace(" ", "."));
        if (s != null)
        {
            s = s.trim();
        }
        return s;
    }
    
    @Override
    public String[] listPages(String page)
    {
        page = page.replace(".", "_").replace("_", ".");
        ArrayList<String> pages = new ArrayList<String>();
        for (String name : props.keySet())
        {
            boolean matches;
            if (page.equals(""))
                matches = !name.contains(".");
            else
                matches = name.startsWith(page + ".")
                        && !(name.substring(page.length() + 1)).contains(".");
            if (matches)
                pages.add(name.substring(page.length() + (page.equals("") ? 0 : 1)));
        }
        return pages.toArray(new String[0]);
    }
}
