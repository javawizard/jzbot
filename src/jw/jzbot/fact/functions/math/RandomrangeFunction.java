package jw.jzbot.fact.functions.math;

import java.util.Random;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import jw.jzbot.utils.Utils;

public class RandomrangeFunction extends Function
{
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        int min = Integer.parseInt(arguments.resolveString(0));
        int max = Integer.parseInt(arguments.resolveString(1));
        int number = Utils.random.nextInt((max - min) + 1);
        number += min;
        sink.write(number);
    }
    
    public String getName()
    {
        return "randomint";
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {randomint|<min>|<max>} -- Returns a random number between "
            + "<min> and <max>, inclusive.";
    }
    
}
