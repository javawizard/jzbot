package jw.jzbot.fact.functions.math;

import java.math.BigDecimal;

import jw.jzbot.JZBot;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import jw.jzbot.fact.functions.EvalFunction;

public class MaxFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        String[] numbers = arguments.resolveString(0).split(" ");
        BigDecimal max = null;
        for (String number : numbers)
        {
            if (max == null)
                max = new BigDecimal(number);
            else
                max = new BigDecimal(number).max(max);
        }
        sink.write(JZBot.toString(max));
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {max|<numbers>} -- Evaluates to the largest of the "
            + "specified numbers. <numbers> should be a space-separated "
            + "list of numbers.";
    }
    
}
