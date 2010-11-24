package jw.jzbot.fact.functions.collections;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import jw.jzbot.fact.exceptions.BreakException;
import jw.jzbot.fact.exceptions.ContinueException;
import jw.jzbot.fact.exceptions.NestedLoopException;
import jw.jzbot.fact.output.DelimitedSink;

public class SplitsetFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        String regex = arguments.resolveString(0);
        String string = arguments.resolveString(1);
        String prefix = arguments.resolveString(2);
        String[] split = string.split(regex);
        if (split.length == 1 && split[0].equals(""))
            split = new String[0];
        for (int i = 0; i < split.length; i++)
        {
            context.getLocalVars().put(prefix + i, split[i]);
        }
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {splitset|<regex>|<string>|<prefix>} -- "
            + "Similar to {split}. Splits the specified around around "
            + "matches of the specified regex, then sets each resulting "
            + "string into a local variable. The first variable is named "
            + "<prefix>0, the second <prefix>1, and so on. If <string> is "
            + "empty, no variables will be set.";
    }
    
    public String getName()
    {
        return "split";
    }
    
}
