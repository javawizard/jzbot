package jw.jzbot.fact.functions.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class MatchintoFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        Matcher matcher =
                Pattern.compile(arguments.resolveString(1)).matcher(
                        arguments.resolveString(0));
        if(!matcher.matches())
            return;
        for (int i = 0; i < matcher.groupCount(); i++)
        {
            if ((i + 2) < arguments.length())
            {
                String varName = arguments.resolveString(i + 2);
                String value = matcher.group(i + 1);
                context.getLocalVars().put(varName, value);
            }
        }
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {matchinto|<text>|<pattern>|<g1>|<g2>|...} -- "
            + "Applies the regex <pattern> against <text>. The first "
            + "match group is assigned to the variable <g1>, the second "
            + "match group is assigned to the variable <g2>, and so on. "
            + "If <pattern> didn't match <text>, none of the variables are set.";
    }
    
}
