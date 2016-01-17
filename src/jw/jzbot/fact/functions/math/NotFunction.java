package jw.jzbot.fact.functions.math;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import jw.jzbot.fact.functions.conditional.IfFunction;

import java.math.BigDecimal;

public class NotFunction extends Function {

  @Override
  public void evaluate(Sink sink, ArgumentList arguments, FactContext context) {
    if (IfFunction.findValue(arguments.resolveString(0))) {
      sink.write("0");
    } else {
      sink.write("1");
    }
  }

  @Override
  public String getHelp(String topic) {
      return "Syntax: {not|<arg>} -- Returns 1 if <arg> is false, 0 if <arg> is true. " +
              "<arg> is interpreted the same as the first argument to {if}.";
  }
}
