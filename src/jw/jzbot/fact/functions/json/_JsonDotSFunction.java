package jw.jzbot.fact.functions.json;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

/**
 * Created by aboyd on 2015-01-18.
 */
public class _JsonDotSFunction extends Function {
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context) {
        sink.write(JSON.encode(arguments.resolveString(0)));
    }

    @Override
    public String getHelp(String topic) {
        return "Syntax: {json.s|<text>} -- Return <text> encoded as a JSON string literal. For example, " +
                "{json.s|foo} would return \"foo\", and {json.s|\"foo\"} would return \"\\\"foo\\\"\".";
    }
}
