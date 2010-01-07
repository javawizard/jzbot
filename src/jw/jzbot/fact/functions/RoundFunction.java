package jw.jzbot.fact.functions;

import java.math.BigDecimal;
import java.math.MathContext;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;


public class RoundFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        sink.write(new BigDecimal(arguments.resolveString(0)).round(new MathContext(Integer
                        .parseInt(arguments.resolveString(1)))).toString());
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {round|<number>|<precision>} -- Rounds the specified "
                + "number to have the specified precision. For example, "
                + "{round|12345|2} is \"12000\", and {round|1.2345|2} is \"1.2\".";
    }
    
}
