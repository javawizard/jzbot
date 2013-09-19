package jw.jzbot.fact.functions.text;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import net.sf.opengroove.common.utils.StringUtils;

public class SplitcharsFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        if (arguments.getString(0).equals(""))
            return;
        if (arguments.getString(0).length() == 1)
        {
            sink.write(arguments.getString(0));
            return;
        }
        char[] text = arguments.getString(0).toCharArray();
        sink.write(text[0]);
        for (int i = 1; i < text.length; i++)
        {
            arguments.get(1, sink);
            sink.write(text[i]);
        }
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {splitchars|<text>|<delimiter>} -- Evaluates to <text>, but "
                + "with <delimiter> inbetween each character in <text>. For example, "
                + "\"{splitchars|hello|-}\" results in \"h-e-l-l-o\".";
    }
    
}
