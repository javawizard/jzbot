package jw.jzbot.fact.functions.li;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class RangeFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{range||<from>||<to>||<varname>||<action>||<delimiter>}} -- "
                + "Exactly the same as "
                + "{{split|| ||{{numberlist||<from>||<to>}}||<varname>||<action>"
                + "||<delimiter>}}, but this function runs considerably faster than "
                + "the split/numberlist combination function. Essentially, this function "
                + "runs <action> once for every number between <from> and <to>\n"
                + "with the local variable <varname> set to the current number. "
                + "The function then evaluates to all of the results of running <action> "
                + "with <delimiter> inbetween them.";
    }
    
}
