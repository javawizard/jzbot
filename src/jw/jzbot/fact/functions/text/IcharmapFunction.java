package jw.jzbot.fact.functions.text;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import jw.jzbot.fact.exceptions.FactoidException;

public class IcharmapFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        String text = arguments.resolveString(0);
        String charmap = arguments.resolveString(1);
        if ((charmap.length() % 2) != 0)
            throw new FactoidException("The char map (\"" + charmap + "\") does not "
                + "have an even number of characters. It must have an even number "
                + "of characters to work. The number of characters in that char "
                + "map is " + charmap.length() + ".");
        StringBuffer from = new StringBuffer();
        StringBuffer to = new StringBuffer();
        for (int i = 0; i < charmap.length(); i += 2)
        {
            from.append(charmap.charAt(i));
            to.append(charmap.charAt(i + 1));
        }
        String fs = from.toString();
        String ts = to.toString();
        for (char c : text.toCharArray())
        {
            int index = fs.indexOf(c);
            if (index == -1)
                sink.write(c);
            else
                sink.write(ts.charAt(index));
        }
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {icharmap|<text>|<charmap>} -- Applies an interlaced character "
            + "map to the specified text. For every character in <text>, <charmap> "
            + "is checked to see if a character with an index congruent to 0 modulo 2 "
            + "is present that is identical to the one in <text>. If there is such "
            + "a character, it is replaced in <text> with the character immediately "
            + "following it in <charmap>. For example, {icharmap|abcbca|a1b2c3} would "
            + "evaluate to \"123231\".";
    }
    
}
