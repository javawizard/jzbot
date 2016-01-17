package jw.jzbot.fact.functions;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class HideFunction extends Function
{

    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
    }

    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {hide|<arg1>|<arg2>|...} -- An alias for {#}. Use {#} instead; {hide} is the old spelling.";
    }

}
