package jw.jzbot.fact.functions.text;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.FactoidException;
import jw.jzbot.fact.Function;

public class AsciiFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        String s = arguments.get(0);
        if (s.length() != 1)
            throw new FactoidException("<char> was \"" + s
                    + "\", which isn't exactly one character.");
        return "" + ((int) s.charAt(0));
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{ascii||<char>}} -- Evaluates to the numerical code that represents the "
                + "ascii character <char>. For example, {{ascii|| }} results in \"32\", "
                + "{{ascii||1}} results in \"49\", and {{ascii||A}} results in \"65\". An "
                + "error will occur if <char> is not a single character.";
    }
    
}
