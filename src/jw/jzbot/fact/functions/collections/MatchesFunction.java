package jw.jzbot.fact.functions.collections;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import jw.jzbot.fact.exceptions.BreakException;
import jw.jzbot.fact.exceptions.ContinueException;
import jw.jzbot.fact.exceptions.NestedLoopException;
import jw.jzbot.fact.output.DelimitedSink;

public class MatchesFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        Pattern pattern = Pattern.compile(arguments.resolveString(1));
        Matcher matcher = pattern.matcher(arguments.resolveString(0));
        String delimiter = "";
        if (arguments.length() > 4)
            delimiter = arguments.getString(4);
        String prefix = arguments.resolveString(2);
        DelimitedSink ds = new DelimitedSink(sink, delimiter);
        while (matcher.find())
        {
            ds.next();
            /*
             * FIXME: track the old values of these variables and reset them to what they
             * were before this was called
             */
            for (int i = 0; i <= matcher.groupCount(); i++)
                context.getLocalVars().put(prefix + "-" + i, matcher.group(i));
            try
            {
                arguments.resolve(3, ds);
            }
            catch (NestedLoopException e)
            {
                e.level--;
                if (e.level == -1)
                {
                    if (e instanceof ContinueException)
                        continue;
                    else if (e instanceof BreakException)
                        break;
                }
                else
                {
                    throw e;
                }
            }
        }
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {matches|<text>|<regex>|<prefix>|<action>|<delimiter>} -- "
            + "Searches through <text> for all pieces of text that match <regex>. "
            + "<action> is then run for each match, and the results of running "
            + "all of the actions are concatenated together with <delimiter> "
            + "between them, and this function then evaluates to that result. "
            + "Each time <action> is run, the local variables <prefix>-1, "
            + "<prefix>-2, ... are set to the results of any matcher groups "
            + "that are present in the regex. <prefix>-0 is set to the full text "
            + "matched by the regex. <delimiter> is optional, and no delimiter will "
            + "be used if it's not present.";
    }
    
}
