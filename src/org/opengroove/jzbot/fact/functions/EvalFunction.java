package org.opengroove.jzbot.fact.functions;

import java.math.BigDecimal;
import java.text.DecimalFormatSymbols;

import org.cheffo.jeplite.JEP;
import org.opengroove.jzbot.JZBot;
import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.FactoidException;
import org.opengroove.jzbot.fact.Function;

public class EvalFunction extends Function
{
    public static DecimalFormatSymbols symbols = new DecimalFormatSymbols();
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        if (arguments.length() == 1)
            return JZBot.evaluateEquation(arguments.get(0), context
                    .getChannel());
        else
            return JZBot.evaluateEquation(arguments.get(1), context
                    .getChannel(), arguments.get(0));
    }
    
    @Override
    public String getName()
    {
        return "eval";
    }
    
    @Override
    public String getHelp(String topic)
    {
        // TODO Auto-generated method stub
        return null;
    }
}
