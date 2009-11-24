package jw.jzbot.fact.functions;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class MapreadFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
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
            if (tokens[0].equals(arguments.get(3)))
                return tokens[1];
        }
        return "";
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{mapread||<listregex>||<itemregex>||<text>||<key>}} -- Converts "
                + "<text> into an associative array by splitting it into a list of items "
                + "around <listregex>, and then by splitting each of those items into two "
                + "pieces around the first occurence of <itemregex>. The item with the "
                + "key named <key> is then looked up, and mapread evaluates to that item's "
                + "value.";
    }
    
}
