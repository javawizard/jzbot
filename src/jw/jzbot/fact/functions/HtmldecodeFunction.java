package jw.jzbot.fact.functions;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;

import org.apache.commons.lang.StringEscapeUtils;

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
