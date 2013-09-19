package jw.jzbot.fact.functions.text;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class CharatFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        int index = Integer.parseInt(arguments.resolveString(0));
        String text = arguments.resolveString(1);
        if (index < 0)
            index += text.length();
        try
        {
            sink.write(text.charAt(index));
        }
        catch (IndexOutOfBoundsException e)
        {
            /*
             * Silently discard; this has the effect of causing this function to evaluate
             * to the empty string.
             */
        }
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {charat|<index>|<text>} -- Evaluates to the "
            + "character at the specified index in the specified string, "
            + "or the empty string if the specified index is out of bounds. "
            + "If the specified index is negative, the string's length is "
            + "added to it before looking up the character.";
    }
    
}
