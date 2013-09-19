package jw.jzbot.fact.functions.text;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class LengthFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        sink.write(arguments.resolveString(0).length());
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {length|<value>} -- Evaluates to the number of characters that are "
                + "in <value>. For example, {length|hello} evaluates to \"5\", and "
                + "{length|hello world} evaluates to \"11\".";
    }
    
}
