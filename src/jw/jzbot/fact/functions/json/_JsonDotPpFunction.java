package jw.jzbot.fact.functions.json;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import org.json.JSONObject;

/**
 * Created by aboyd on 2016-02-05.
 */
public class _JsonDotPpFunction extends Function {
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context) {
        int indent = 2;
        if (arguments.length() > 1) {
            indent = Integer.parseInt(arguments.resolveString(1));
        }

        Object json = JSON.parse(arguments.resolveString(0));
        sink.write(JSONObject.valueToString(json, indent, 0));
    }

    @Override
    public String getHelp(String topic) {
        return "Syntax: {json.pp|<json>|<indent>} - Pretty print <json> with the specified indent, which defaults " +
                "to 2 if only one argument is given.";
    }}
