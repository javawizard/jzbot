package jw.jzbot.fact.functions.text;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class HopFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        String text = arguments.resolveString(0);
        String insert = arguments.resolveString(1);
        int number = Integer.parseInt(arguments.resolveString(2));
        StringBuffer output = new StringBuffer();
        for (int i = 0; i < text.length(); i++)
        {
            char c = text.charAt(i);
            output.append(c);
            if (((i + 1) % number) == 0 && i != (text.length() - 1))
                output.append(insert);
        }
        sink.write(output.toString());
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {hop|<text>|<insert>|<number>} -- Evaluates to <text>, but "
            + "with <insert> inserted every <number> characters. This function "
            + "starts counting at the beginning of the string. For example, "
            + "{hop|12345678| |3} would evaluate to \"123 456 78\".";
    }
    
}
