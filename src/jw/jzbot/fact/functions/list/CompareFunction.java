package jw.jzbot.fact.functions.list;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import jw.jzbot.fact.functions.conditional.IfFunction;

public class CompareFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        String first = arguments.get(0);
        String second = arguments.get(1);
        boolean caseSensitive = true;
        boolean forIf = false;
        if (arguments.length() > 2)
        {
            caseSensitive = IfFunction.findValue(arguments.get(2));
            if (arguments.length() > 3)
            {
                forIf = IfFunction.findValue(arguments.get(3));
            }
        }
        int result;
        // We use Math.signum since compareTo can return any value that is
        // positive or negative instead of 1 or -1, respectively, which we don't
        // want. Math.signum will convert that for us.
        if (caseSensitive)
            result = (int) Math.signum(first.compareTo(second));
        else
            result = (int) Math.signum(first.compareToIgnoreCase(second));
        if (forIf && result == -1)
            result = 0;
        return "" + result;
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{compare||<first>||<second>||<case>||<if>}} -- Compares <first> with "
                + "<second> to see which would come first in a dictionary. This function then "
                + "evaluates to -1 if <first> comes before <second>, 0 if they are the same, "
                + "and 1 if <first> comes after <second>. <case> and <if> are optional, and "
                + "default to 1 and 0, respectively. When <case> is 1, case is taken into account "
                + "(IE \"A\" comes before \"a\"), and when\n"
                + "it is 0, case is ignored (IE \"A\" and \"a\" are the same). When <if> is 1, "
                + "this function evaluates to 0 instead of -1 if <first> comes before <second>, "
                + "thereby making the output suitable for directly passing to the {{if}} function. "
                + "When <if> is 0, -1, 0, and 1 are returned as specified above.";
    }
    
}
