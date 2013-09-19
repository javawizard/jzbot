package jw.jzbot.fact.functions.math;

import java.math.BigDecimal;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class _SlashSlashFunction extends Function {
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context) {
        BigDecimal total = new BigDecimal(arguments.resolveString(0));
        for (int i = 1; i < arguments.length(); i++)
            total = total.divide(new BigDecimal(arguments.resolveString(i)));
        sink.write(Integer.toString(total.intValue()));
        
    }
    
    @Override
    public String getHelp(String topic) {
        return "Syntax: {//|<arg1>|<arg2>|...} -- Returns the result of dividing <arg1> by <arg2>, the by <arg3>, and so on, but rounded down to the nearest integer";
    }
}
