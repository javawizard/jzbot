package jw.jzbot.fact.functions;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class RestrictFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        String text = arguments.get(0);
        int length = Integer.parseInt(arguments.get(1));
        if (text.length() <= length || length <= 3)
            return text;
        return text.substring(0, length - 3) + "...";
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{restrict||<text>||<length>}} -- Evaluates to <text> if its length "
                + "is shorter than <length>. Otherwise, evaluates to the first <length>-3 "
                + "characters of <text>, with three dot characters on the end to form an "
                + "ellipsis. For example, {{restrict||Hello everyone||15}} would be \n"
                + "\"Hello everyone\", but {{restrict||Hello everyone||11}} would be "
                + "\"Hello ev...\".";
    }
    
}
