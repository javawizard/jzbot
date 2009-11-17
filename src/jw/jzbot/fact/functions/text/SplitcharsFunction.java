package jw.jzbot.fact.functions.text;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import net.sf.opengroove.common.utils.StringUtils;


public class SplitcharsFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        if (arguments.get(0).equals(""))
            return "";
        if (arguments.get(0).length() == 1)
            return arguments.get(0);
        StringBuffer b = new StringBuffer();
        char[] text = arguments.get(0).toCharArray();
        b.append(text[0]);
        for (int i = 1; i < text.length; i++)
        {
            b.append(arguments.get(1));
            b.append(text[i]);
        }
        return b.toString();
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{splitchars||<text>||<delimiter>}} -- Evaluates to <text>, but "
                + "with <delimiter> inbetween each character in <text>. For example, "
                + "\"{{splitchars||hello||-}}\" results in \"h-e-l-l-o\".";
    }
    
}
