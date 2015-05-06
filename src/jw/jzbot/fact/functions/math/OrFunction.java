package jw.jzbot.fact.functions.math;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import jw.jzbot.fact.functions.conditional.IfFunction;

public class OrFunction extends Function {
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context) {
        boolean result = false;
        for (int i = 0; i < arguments.length(); i++) {
            if (IfFunction.findValue(arguments.resolveString(i))) {
                result = true;
                break;
            }
        }
        sink.write(result ? "1" : "0");
    }
    
    @Override
    public String getHelp(String topic) {
        return "Syntax: {or|<arg1>|<arg2>|...} -- Returns 1 if any of its arguments are true, 0 if all of them are false. " +
                "Arguments are interepreted the same as the first argument to {if}. Only arguments before and including the first argument " +
                "whose value is true will be evaluated.";
    }
}
