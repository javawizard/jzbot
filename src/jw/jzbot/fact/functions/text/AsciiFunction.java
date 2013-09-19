package jw.jzbot.fact.functions.text;

import net.sf.opengroove.common.utils.StringUtils;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import jw.jzbot.fact.exceptions.FactoidException;
import jw.jzbot.utils.Utils;

public class AsciiFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        String s = arguments.resolveString(0);
        String[] strs = new String[s.length()];
        for (int i = 0; i < strs.length; i++)
            strs[i] = "" + ((int) s.charAt(i));
        sink.write(StringUtils.delimited(strs, " "));
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {ascii|<char>} -- Evaluates to the numerical code that represents the "
            + "ascii character <char>. For example, {ascii| } results in \"32\", "
            + "{ascii|1} results in \"49\", and {ascii|A} results in \"65\". If "
            + "<char> contains multiple characters, the result will be the value "
            + "of each character separated by spaces. "
            + "TODO: Right now, this function, instead of returning the ascii char value, "
            + "returns the value of the character in whatever charset you've configured "
            + "the bot with.";
    }
    
}
