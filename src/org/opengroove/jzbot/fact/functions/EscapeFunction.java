package org.opengroove.jzbot.fact.functions;

import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.Function;

public class EscapeFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        String text = arguments.get(0);
        StringBuffer buffer = new StringBuffer();
        for (char c : text.toCharArray())
        {
            if (".$.%.{.}.|.".contains("." + c + "."))
                buffer.append("\\").append(c);
            else if (c == '\n')
                buffer.append("\\n");
            else if (c < 32 || c > 126)
                buffer.append("{{char||" + ((int) c) + "}}");
        }
        text = text.replace("\\", "\\\\");
        text = text.replaceAll("(\\$|\\%|\\{|\\}|\\|)", "\\$1");
        text = text.replace("\r", "{{char||13}}");
        text = text.replace("\n", "\\n");
        return text;
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{escape||<text>}} -- Escapes <text> with backslashes, \"\\n\", and "
                + "such, so that the resulting text, when embedded directly within a "
                + "factoid, would evaluate to <text>. For example, all \"|\" characters, "
                + "\"{\" characters, and \"}\" characters are prefixed with a \"\\\". "
                + "<text> can also contain non-ascii-visible characters, and these will "
                + "\nbe replaced with a call to the {{char}} function.";
    }
    
}
