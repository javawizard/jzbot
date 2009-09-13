package org.opengroove.jzbot.fact;

public abstract class Function
{
    public abstract String getName();
    
    public abstract String evaluate(ArgumentList arguments, FactContext context);
}
