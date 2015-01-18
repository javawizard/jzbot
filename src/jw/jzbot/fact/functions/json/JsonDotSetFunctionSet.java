package jw.jzbot.fact.functions.json;

import jw.jzbot.fact.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by aboyd on 2015-01-18.
 */
public class JsonDotSetFunctionSet implements FunctionSet {
    @Override
    public Map<String, Function> getFunctions() {
        Map<String, Function> map = new HashMap<String, Function>();
        map.put("json.set", new JsonDotSetFunction(false, false));
        map.put("json.eset", new JsonDotSetFunction(true, false));
        map.put("json.sete", new JsonDotSetFunction(false, true));
        map.put("json.esete", new JsonDotSetFunction(true, true));
        return map;
    }

    private static class JsonDotSetFunction extends Function {
        private boolean encodedKeys;
        private boolean encodedValue;

        public JsonDotSetFunction(boolean encodedKeys, boolean encodedValue) {
            this.encodedKeys = encodedKeys;
            this.encodedValue = encodedValue;
        }

        @Override
        public void evaluate(Sink sink, ArgumentList arguments, FactContext context) {
            JSON.Nested nested = new JSON.Nested(arguments.resolveString(0), true);
            nested.down(arguments.subList(1, arguments.length() - 2), encodedKeys);
            nested.set(arguments.resolveString(arguments.length() - 1), encodedValue);
            sink.write(nested.encodedToplevel());
        }

        @Override
        public String getHelp(String topic) {
            return "Syntax: {json.[e]set[e]|<json>|<key1>|<key2>|...|<value>} -- Set the value at the specified path in " +
                    "the specified JSON data. TODO: Need more documentation";
        }
    }
}
