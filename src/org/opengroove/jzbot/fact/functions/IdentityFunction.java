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
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public String getName()
    {
        return "identity";
    }
    
}
