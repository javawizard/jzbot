package jw.jzbot.fact.functions.conditional;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class EqFunction extends Function
{

  @Override
  public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
  {
    if (arguments.resolveString(0).equals(arguments.resolveString(1))) {
      sink.write("1");
    } else {
      sink.write("0");
    }
  }

  public String getName()
  {
    return "eq";
  }

  @Override
  public String getHelp(String topic)
  {
    return "Syntax: {eq|<first>|<second>} -- "
            + "Evaluates to 1 if the strings <first> and <second> are equal, 0 otherwise. Unlike "
            + "{ifeq}, case is significant, so {eq|A|a} returns 0. For a case-insensitive comparison, "
            + "use {ceq}.";
  }
}
