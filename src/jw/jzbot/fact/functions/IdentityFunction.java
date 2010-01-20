package jw.jzbot.fact.functions;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import jw.jzbot.fact.output.DelimitedSink;

/**
 * A function that takes as many arguments as are desired, and returns a string consisting
 * of those arguments separated by spaces. It's generally intended to be used with only
 * one argument, and when used as such it acts as if it weren't even there.
 * 
 * @author Alexander Boyd
 * 
 */
public class IdentityFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        DelimitedSink result = new DelimitedSink(sink, " ");
        for (int i = 0; i < arguments.length(); i++)
        {
            result.next();
            arguments.resolve(i, result);
        }
    }
    
    public String getName()
    {
        return "identity";
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {identity|<arg1>|<arg2>|...} -- Evaluates to "
                + "all of its arguments, concatenated and with a space inbetween. "
                + "Essentially the same as \"<arg1> <arg2> ...\". This "
                + "function is primarily intended for the factoid language interpreter's "
                + "internal use but can be used in factoids as well.";
    }
    
}
