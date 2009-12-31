package jw.jzbot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;

public class FactProps extends HashMap<String, String>
{
    public void load(Reader rd) throws IOException
    {
        BufferedReader in = new BufferedReader(rd);
        String line;
        while ((line = in.readLine()) != null)
        {
            if (line.trim().equals(""))
                continue;
            else if (line.startsWith("#"))
                continue;
            // we have an actual property here.
            String valueLineText = null;
            String key = null;
            String value = null;
            if (line.startsWith("<"))
            {
                // line is a heredoc
                line = line.substring(1);
                if (line.startsWith("<"))
                    line = line.substring(1);
                String heredocTerminator = line.trim();
                String keyContents = "";
                // now we read new lines until we hit one that is equal to the heredoc
                // terminator, exactly
                line = in.readLine();
                while (!line.equals(heredocTerminator))
                {
                    keyContents += "\n";
                    keyContents += line;
                    line = in.readLine();
                }
                if (keyContents.length() > 0)
                    keyContents = keyContents.substring(1);
                line = in.readLine();
                if (line == null)
                    throw new RuntimeException("Missing value after key heredoc");
                valueLineText = line;
                key = keyContents;
            }
            else
            {
                // line isn't a heredoc. Key is the first word, valueLineText is
                // everything after.
                int spaceIndex = line.indexOf(' ');
                if (spaceIndex == -1)
                {
                    key = line;
                    valueLineText = "";
                }
                else
                {
                    key = line.substring(0, spaceIndex);
                    valueLineText = line.substring(spaceIndex + 1);
                }
            }
            // now we parse the value.
            if (valueLineText.startsWith(":"))
            {
                // line is normal
                value = valueLineText.substring(1);
            }
            else if (valueLineText.startsWith("<"))
            {
                // line is a heredoc
                valueLineText = valueLineText.substring(1);
                if (valueLineText.startsWith("<"))
                    valueLineText = valueLineText.substring(1);
                String heredocTerminator = valueLineText.trim();
                value = "";
                // now we read new lines until we hit one that is equal to the heredoc
                // terminator, exactly
                line = in.readLine();
                while (!line.equals(heredocTerminator))
                {
                    value += "\n";
                    value += line;
                    line = in.readLine();
                }
                if (value.length() > 0)
                    value = value.substring(1);
            }
            else
            {
                // line is implicitly normal
                value = valueLineText;
            }
            // now we add the property
            put(key, value);
        }
    }
    
    /**
     * Returns the value of the specified property, or <tt>def</tt> if such a property
     * does not exist.
     * 
     * @param key
     *            The key of the property to get
     * @param def
     *            The value to return if such a property does not exist
     * @return The value of the specified property, or <tt>def</tt> if there is no such
     *         property
     */
    public String get(String key, String def)
    {
        String value = get(key);
        if (value == null)
            return def;
        return value;
    }
}
