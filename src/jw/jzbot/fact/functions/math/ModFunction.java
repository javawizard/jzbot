package jw.jzbot.fact.functions.math;

import java.math.BigDecimal;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class ModFunction extends Function {
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context) {
        sink.write((new BigDecimal(arguments.resolveString(0))
                .remainder(new BigDecimal(arguments.resolveString(1))))
                .toString());
    }
    
    @Override
    public String getHelp(String topic) {
        return "Syntax: {mod|<number1>|<number2>} -- Returns the remainder of dividing number1 by number2.";
    }
    
}
