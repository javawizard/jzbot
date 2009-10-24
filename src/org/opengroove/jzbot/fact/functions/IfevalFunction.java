package org.opengroove.jzbot.fact.functions;

import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.Function;

public class IfevalFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{ifeval||<statement>||<trueaction>||<falseaction>}} -- "
                + "Evaluates <statement> as per {{eval||<statement>}}, then runs "
                + "<trueaction> if the result of the evaluation is 1 or <falseaction> "
                + "if the result of the evaluation is 0. This function acts exactly "
                + "like {{if||{{eval||<statement}}||<trueaction>||<falseaction>}}. \n"
                + "<falseaction> is optional, and if not present {{ifeval}} will act "
                + "as if <falseaction> were empty.";
    }
    
}
