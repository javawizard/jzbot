package jw.jzbot.fact.functions.math;

import java.math.BigDecimal;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class _PlusFunction extends Function {
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context) {
        BigDecimal total = new BigDecimal("0");
        for (int i = 0; i < arguments.length(); i++)
            total = total.add(new BigDecimal(arguments.resolveString(i)));
        sink.write(total.toString());
    }
    
    @Override
    public String getHelp(String topic) {
        return "Syntax: {+|<arg1>|<arg2>|...} -- Returns the numerical sum of the specified numbers.";
    }
}
