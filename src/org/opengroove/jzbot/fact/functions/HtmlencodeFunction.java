package org.opengroove.jzbot.fact.functions;

import org.apache.commons.lang.StringEscapeUtils;
import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.Function;

public class HtmlencodeFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        return StringEscapeUtils.escapeHtml(arguments.get(0));
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{htmlencode||<text>}} -- Escapes all HTML special characters "
                + "in <text>. For example, \"<\" gets changed to \"&lt;\".";
    }
    
}
