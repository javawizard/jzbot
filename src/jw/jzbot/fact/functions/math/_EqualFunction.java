package jw.jzbot.fact.functions.math;

import java.math.BigDecimal;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class _EqualFunction extends Function {

    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context) {
        sink.write((new BigDecimal(arguments.resolveString(0))
                .compareTo(new BigDecimal(arguments.resolveString(1))) == 0) ? 1
                : 0);
    }

    @Override
    public String getHelp(String topic) {
        return "Syntax: {=|<number1>|<number2>} -- Returns 1 if <number1> is equal to <number2>, 0 otherwise. "
        + "This is a numerical comparison; to compare exact string content, use {eq} instead.";
    }

}
