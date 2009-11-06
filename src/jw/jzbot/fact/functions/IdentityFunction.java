package jw.jzbot.fact.functions;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;

/**
 * A function that takes as many arguments as are desired, and returns a string
 * consisting of those arguments separated by spaces. It's generally intended to
 * be used with only one argument, and when used as such it acts as if it
 * weren't even there.
 * 
 * @author Alexander Boyd
 * 
 */
public class IdentityFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < arguments.length(); i++)
        {
            buffer.append(" ").append(arguments.get(i));
        }
        if (buffer.length() == 0)
            return "";
        else
            return buffer.substring(1);
    }
    
    public String getName()
    {
        return "identity";
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{identity||<argument1>||<argument2>||...}} -- Evaluates to "
                + "all of its arguments, concatenated and with a space inbetween. "
                + "Essentially the same as \"<argument1> <argument2> ...\". This "
                + "function is primarily intended for the bot's internal use but can be "
                + "used in factoids as well.";
    }
    
}
