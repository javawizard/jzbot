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
        sink.write(arguments.resolveString(0).replaceAll(arguments.resolveString(1),
                arguments.resolveString(2)));
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {regexreplace|<text>|<regex>|<replacement>|...} -- Finds "
            + "all pieces of text within <text> that match <regex> and replaces "
            + "them with <replacement>. <replacement> can contain references to "
            + "matcher groups within <regex>.";
    }
    
}
