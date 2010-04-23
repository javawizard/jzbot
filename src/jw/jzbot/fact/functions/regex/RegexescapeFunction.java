package jw.jzbot.fact.functions.regex;

import java.util.regex.Pattern;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class RegexescapeFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        sink.write(Pattern.quote(arguments.resolveString(0)));
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {regexescape|<text>} -- Escapes <text> in such a way that "
            + "the resulting text can be embedded within a regular expression and "
            + "will be interpreted as literal text.";
    }
    
}
