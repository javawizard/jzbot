package org.opengroove.jzbot.fact.functions;

import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.Function;

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
    
    @Override
    public String getName()
    {
        return "identity";
    }

    @Override
    public String getHelp(String topic)
    {
        // TODO Auto-generated method stub
        return null;
    }
    
}
