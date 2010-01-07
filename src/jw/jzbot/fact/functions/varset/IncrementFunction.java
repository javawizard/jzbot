package jw.jzbot.fact.functions.varset;

import java.math.BigInteger;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class IncrementFunction extends Function
{
    public static final BigInteger one = new BigInteger("1");
    public static final BigInteger minusOne = one.negate();
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        modUp(context, arguments.resolveString(0));
    }
    
    public static String modify(FactContext context, String name, BigInteger delta)
    {
        String value = context.getLocalVars().get(name);
        if (value == null)
            value = "0";
        BigInteger number = new BigInteger(value);
        number = number.add(delta);
        String newString = number.toString();
        context.getLocalVars().put(name, newString);
        return newString;
    }
    
    public static String modUp(FactContext context, String name)
    {
        return modify(context, name, one);
    }
    
    public static String modDown(FactContext context, String name)
    {
        return modify(context, name, minusOne);
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {increment|<varname>} -- Gets the numeric value in the specified "
                + "local variable, adds one to it, and sets it back into the local variable. "
                + "If the specified local variable does not exist, this function will instead "
                + "set it to 1. If the specified local variable does exist but is not a "
                + "number (or if it exists but is the empty string), an error will occur.";
    }
    
}
