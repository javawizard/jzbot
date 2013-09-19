package jw.jzbot.fact.functions.text;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class IndexofFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        // TODO: consider changing to resolveString instead of getString
        if (arguments.length() == 2)
            sink.write(arguments.getString(1).indexOf(arguments.getString(0)));
        else
            sink.write(arguments.getString(1).indexOf(arguments.getString(0),
                    Integer.parseInt(arguments.getString(2))));
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {indexof|<substring>|<string>|<from>} -- Evaluates to the index within "
                + "<string> that <substring> first occurs, or -1 if <substring> isn't present "
                + "anywhere within <string>. <from> is optional, and if it's present it "
                + "specifies an index that the search will begin at.";
    }
    
}
