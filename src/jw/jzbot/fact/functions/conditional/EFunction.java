package jw.jzbot.fact.functions.conditional;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class EFunction extends Function
{
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        if (arguments.resolveString(0).equals(""))
            sink.write("1");
        else
            sink.write("0");
    }

    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {e|<text>} -- Evaluates "
                + "to 1 if <text> is empty, 0 otherwise. Unlike {ife}, whitespace is not considered to be empty, "
                + "so {e| } returns 1; if you need {ife}'s behavior, use {e|{trim|...}} instead.";
    }

}
