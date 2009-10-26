package org.opengroove.jzbot.fact.functions;

import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.Function;

public class Mapinline extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        String[] strings = arguments.get(2).split(arguments.get(0));
        if (strings.length == 1 && strings[0].equals(""))
            return "";
        for (String s : strings)
        {
            String[] tokens = s.split(arguments.get(1), 2);
            if (tokens.length == 1)
                tokens = new String[]
                {
                        tokens[0], ""
                };
            context.getLocalVars().put(arguments.get(3) + tokens[0], tokens[1]);
        }
        return "";
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{mapinline||<listregex>||<itemregex>||<text>||<prefix>}} -- "
                + "Converts <text> into an associative array by splitting it into a list "
                + "of items around <listregex>, and then by splitting each of those "
                + "items into two pieces around the first occurence of <itemregex>. "
                + "Each item in this associative array is then stored as a local variable, \n"
                + "with the prefix plus the item's name being the key and the item's "
                + "value being the value of the variable.";
    }
}
