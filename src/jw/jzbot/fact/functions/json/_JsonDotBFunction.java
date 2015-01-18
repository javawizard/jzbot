package jw.jzbot.fact.functions.json;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import jw.jzbot.fact.functions.conditional.IfFunction;

/**
 * Created by aboyd on 2015-01-18.
 */
public class _JsonDotBFunction extends Function {
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context) {
        Boolean result = IfFunction.findValueOrNull(arguments.resolveString(0));
        if (result.equals(Boolean.TRUE))
            sink.write("true");
        else if (result.equals(Boolean.FALSE))
            sink.write("false");
    }

    @Override
    public String getHelp(String topic) {
        return "Syntax; {json.b|<value>} -- Convert <value> to a JSON boolean. The same rules used by {if} will be " +
                "applied to determine whether <value> is true or false, and \"true\" or \"false\" returned " +
                "accordingly. If <value> is neither true nor false, the behavior of {json.b} is undefined - currently " +
                "{json.b} evaluates to nothing in such a case, but I might change that to, say, throw an exception " +
                "later (or I might decide that returning an empty string is now defined behavior).";
    }
}
