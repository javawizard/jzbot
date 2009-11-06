package jw.jzbot.fact.functions;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;

import org.apache.commons.lang.StringEscapeUtils;

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
