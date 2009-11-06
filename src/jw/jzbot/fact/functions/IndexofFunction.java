package jw.jzbot.fact.functions;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;

public class IndexofFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        if (arguments.length() == 2)
            return "" + arguments.get(1).indexOf(arguments.get(0));
        else
            return ""
                    + arguments.get(1).indexOf(arguments.get(0),
                            Integer.parseInt(arguments.get(2)));
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{indexof||<substring>||<string>||<from>}} -- Evaluates to the index within "
                + "<string> that <substring> first occurs, or -1 if <substring> isn't present "
                + "anywhere within <string>. <from> is optional, and if it's present it "
                + "specifies an index that the search will begin at.";
    }
    
}
