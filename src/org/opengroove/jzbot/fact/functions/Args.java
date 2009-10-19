package org.opengroove.jzbot.fact.functions;

import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.Function;
import org.opengroove.jzbot.fact.FunctionReference;
import org.opengroove.jzbot.fact.Literal;
import org.opengroove.jzbot.fact.Sequence;

public class Args extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        String regex = arguments.get(0);
        String text = arguments.get(1);
        String[] tokens = (text.equals("") ? new String[0] : text.split(regex));
        Sequence sequence = new Sequence();
        for (String s : tokens)
            sequence.add(new Literal(s));
        FunctionReference function = new FunctionReference(sequence);
        return function.resolve(context);
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{args||<regex>||<text>}} -- Splits <text> around the regular "
                + "expression <regex>, then runs the function named by the first string "
                + "in this sublist, passing the rest of the strings as arguments. For "
                + "example, \"{{args||,||sendmessage,%0%,Hello}}\" would function exactly "
                + "the same as \"{{sendmessage||%0%||Hello}}\".";
    }
    
}
