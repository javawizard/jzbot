package org.opengroove.jzbot.fact.functions;

import java.io.UnsupportedEncodingException;

import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.FactoidException;
import org.opengroove.jzbot.fact.Function;

public class EscapeFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        String text = arguments.get(0);
        return escape(text);
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{escape||<text>}} -- Escapes <text> with backslashes, \"\\n\", and "
                + "such, so that the resulting text, when embedded directly within a "
                + "factoid, would evaluate to <text>. For example, all \"|\" characters, "
                + "\"{\" characters, and \"}\" characters are prefixed with a \"\\\". "
                + "<text> can also contain non-ascii-visible characters, and these will "
                + "\nbe replaced with a call to the {{char}} function. Currently, this "
                + "doesn't correctly support UTF-8.";
    }
    
    /**
     * Returns the specified text, escaped so that all special constructs
     * according to the factoid language are properly escaped.
     * 
     * @param text
     * @return
     */
    public static String escape(String text)
    {
        StringBuffer buffer = new StringBuffer();
        try
        {
            for (byte b : text.getBytes("US-ASCII"))
            {
                char c = (char) b;
                c = (char) (c % 256);
                if (".$.%.{.}.|.\\.".contains("." + c + "."))
                    buffer.append("\\").append(c);
                else if (c == '\n')
                    buffer.append("\\n");
                else if (c < 32 || c > 126)
                    buffer.append("{{char||" + ((int) c) + "}}");
                else
                    buffer.append(c);
            }
        }
        catch (UnsupportedEncodingException e)
        {
            throw new FactoidException("Charset error", e);
        }
        return buffer.toString();
    }
    
}
