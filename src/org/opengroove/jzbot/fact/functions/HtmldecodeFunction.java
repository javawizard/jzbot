package org.opengroove.jzbot.fact.functions;

import org.apache.commons.lang.StringEscapeUtils;
import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.Function;

public class HtmldecodeFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        return StringEscapeUtils.unescapeHtml(arguments.get(0));
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{htmldecode||<text>}} -- Decodes all HTML-escaped characters "
                + "in the specified text. This is the opposite of {{htmlencode}}.";
    }
    
}
