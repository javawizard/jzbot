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
        String lastPropertyKey = null;
        boolean lastPropertyNoNewline = false;
        while ((line = in.readLine()) != null)
        {
            if (line.equals(""))
                continue;
            else if (line.startsWith("#"))
                continue;
            else if (line.startsWith(" "))
            {
                /*
                 * we have an inline continuation of a property value. We'll look up the
                 * property whose name is lastPropertyKey, and then append this line, with
                 * the space removed, onto it, optionally with a newline.
                 * 
                 * Specifically, with regard to the newline, we figure out the
                 * continuation and whether to add a newline according to these rules, in
                 * the order given:
                 * 
                 * If the line starts with " :", the first two characters are removed and
                 * the result is the continuation. A newline is added.
                 * 
                 * If the line starts with "  :", the first three characters are removed
                 * and the result is the continuation. A newline is not added.
                 * 
                 * If the line starts with "  ", the first two characters are removed and
                 * the result is the continuation. A newline is not added.
                 * 
                 * If the line starts with " ", the first character is removed and the
                 * result is the continuation. A newline is added.
                 */
                if (lastPropertyKey == null)
                    throw new RuntimeException("A line continuation (IE a line that "
                            + "started with a space character) was used, "
                            + "but no property has been declared yet.");
                String existingValue = get(lastPropertyKey);
                String continuation;
                boolean newline;
                if (line.startsWith(" :"))
                {
                    continuation = line.substring(2);
                    newline = true;
                }
                else if (line.startsWith("  :"))
                {
                    continuation = line.substring(3);
                    newline = false;
                }
                else if (line.startsWith("  "))
                {
                    continuation = line.substring(2);
                    newline = false;
                }
                else
                // line is guaranteed to start with a space character as a result of the
                // if statement surrounding this whole block
                {
                    continuation = line.substring(1);
                    newline = true;
                }
                if (lastPropertyNoNewline)
                    newline = false;
                lastPropertyNoNewline = false;
                if (newline)
                    existingValue += "\n";
                existingValue += continuation;
                put(lastPropertyKey, existingValue);
                continue;
            }
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
            lastPropertyNoNewline = false;
            if (valueLineText.startsWith(":"))
            {
                // line is normal
                value = valueLineText.substring(1);
            }
            else if (valueLineText.equals("-"))
            {
                // line is blank, and will be added to by a continuation. This means we
                // should omit the first newline in the continuation following this
                // property, if there is one.
                value = "";
                lastPropertyNoNewline = true;
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
            lastPropertyKey = key;
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
