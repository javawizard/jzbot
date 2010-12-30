package jw.jzbot.fact.functions.text;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class RegexreplaceFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        String value = arguments.resolveString(0);
        for (int i = 1; i < arguments.length(); i += 2)
        {
            value =
                    value.replaceAll(arguments.resolveString(i), arguments
                            .resolveString(i + 1));
        }
        sink.write(value);
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {regexreplace|<text>|<regex>|<replacement>|<regex2>|"
            + "<replacement2>|...} -- Finds "
            + "all pieces of text within <text> that match <regex> and replaces "
            + "them with <replacement>, then searches the resulting text for "
            + "matches to <regex2> and replaces them with <replacement2>, "
            + "and so on. The replacements can contain references to "
            + "matcher groups within the regexes..";
    }
    
}
