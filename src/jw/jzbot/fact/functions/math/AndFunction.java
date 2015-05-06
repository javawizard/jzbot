package jw.jzbot.fact.functions.math;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import jw.jzbot.fact.functions.conditional.IfFunction;

import java.math.BigDecimal;

public class AndFunction extends Function {
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context) {
        boolean result = true;
        for (int i = 0; i < arguments.length(); i++) {
            if (!IfFunction.findValue(arguments.resolveString(i))) {
                result = false;
                break;
            }
        }
        sink.write(result ? "1" : "0");
    }
    
    @Override
    public String getHelp(String topic) {
        return "Syntax: {and|<arg1>|<arg2>|...} -- Returns 1 if all of its arguments are true, 0 if any of them are false. " +
                "Arguments are interepreted the same as the first argument to {if}. Only arguments before and including the first argument " +
                "whose value is false will be evaluated.";
    }
}
