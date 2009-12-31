package jw.jzbot;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Set;

import jw.jzbot.fact.functions.EscapeFunction;

public class Factpack
{
    public static class FactpackEntry
    {
        /**
         * The name of the factoid.
         */
        public String name;
        /**
         * The factoid's contents.
         */
        public String contents;
        /**
         * A command to run to determine if this should be restricted.
         */
        public String restrict;
        /**
         * A command to run to determine if this should be a library factoid.
         */
        public String library;
        /**
         * A command to run to determine the factpack's name.
         */
        public String rename;
    }
    
    public String name;
    public String author;
    public String description;
    public String preinstall;
    public String postinstall;
    public FactpackEntry[] factoids;
    
    public static Factpack parse(String text)
    {
        Factpack pack = new Factpack();
        // Properties props = new Properties(createDefaults());
        FactProps props = new FactProps();
        try
        {
            props.load(new StringReader(text));
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        String name = props.get("name");
        verify(name, "No name was present.");
        verifyMatch(name, "^[0-9a-z\\-\\.]*$", "Invalid factpack name \"" + name
                + "\". The name can consist only of digits, "
                + "lower-case letters, hyphens, and periods.");
        pack.name = name;
        pack.author = props.get("author", "(unspecified)");
        pack.description = props.get("description", "(no description)");
        pack.preinstall = props.get("preinstall", "");
        pack.postinstall = props.get("postinstall", "");
        Set<String> names = props.keySet();
        ArrayList<FactpackEntry> entries = new ArrayList<FactpackEntry>();
        for (String prop : names)
        {
            if (!prop.startsWith(">"))
                continue;
            FactpackEntry entry = new FactpackEntry();
            // entry.target = "" + prop.charAt(0);
            entry.name = prop.substring(2);
            entry.contents = props.get(prop);
            entry.restrict = props.get("restrict." + prop, "0");
            entry.rename = props.get("rename." + prop, EscapeFunction.escape(entry.name));
            entry.library = props.get("library." + prop, "0");
            entries.add(entry);
        }
        pack.factoids = entries.toArray(new FactpackEntry[0]);
        return pack;
    }
    
    private static void verifyMatch(String s, String regex, String message)
    {
        if (!s.matches(regex))
            throw new IllegalArgumentException(message);
    }
    
    private static Properties createDefaults()
    {
        Properties props = new Properties();
        props.setProperty("author", "(unspecified)");
        props.setProperty("description", "(no description)");
        props.setProperty("preinstall", "");
        props.setProperty("postinstall", "");
        return props;
    }
    
    /**
     * Throws an exception if <tt>text</tt> is null.
     * 
     * @param text
     */
    private static void verify(String text, String message)
    {
        if (text == null)
            throw new IllegalArgumentException(message);
    }
}
