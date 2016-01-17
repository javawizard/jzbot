package jw.jzbot.fact.functions.conditional;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class CeqFunction extends Function
{

  @Override
  public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
  {
    if (arguments.resolveString(0).equalsIgnoreCase(arguments.resolveString(1))) {
      sink.write("1");
    } else {
      sink.write("0");
    }
  }

  public String getName()
  {
    return "ceq";
  }

  @Override
  public String getHelp(String topic)
  {
    return "Syntax: {ceq|<first>|<second>} -- "
            + "Evaluates to 1 if the strings <first> and <second> are equal, ignoring case, 0 otherwise. Unlike "
            + "{ifceq}, this is a case-insensitive comparison, so {eq|A|a} returns 1. For a case-sensitive comparison, "
            + "use {eq}.";
  }
}
