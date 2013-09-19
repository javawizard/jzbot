package jw.jzbot.fact.functions.math;

import java.math.BigDecimal;

import jw.jzbot.JZBot;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import jw.jzbot.fact.functions.EvalFunction;

public class MinFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        String[] numbers = arguments.resolveString(0).split(" ");
        BigDecimal min = null;
        for (String number : numbers)
        {
            if (min == null)
                min = new BigDecimal(number);
            else
                min = new BigDecimal(number).min(min);
        }
        sink.write(JZBot.toString(min));
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {min|<numbers>} -- Evaluates to the smallest of the "
            + "specified numbers. <numbers> should be a space-separated "
            + "list of numbers.";
    }
    
}
