package jw.jzbot.fact.functions.conditional;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class IfeFunction extends Function
{
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        if (arguments.get(0).trim().equals(""))
            return arguments.get(1);
        else if (arguments.length() > 2)
            return arguments.get(2);
        else
            return "";
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{ife||<compare>||<trueaction>||<falseaction>}} -- Evaluates "
                + "to <trueaction> if <compare> is either empty or made up only of "
                + "whitespace, or <falseaction> if <compare> is not empty and is not made "
                + "up entirely of whitespace. <falseaction> is optional, and if it's not "
                + "present {{ife}} will act as if <falseaction> were empty. This function "
                + "is the opposite of {{ifne}}.";
    }
    
}
