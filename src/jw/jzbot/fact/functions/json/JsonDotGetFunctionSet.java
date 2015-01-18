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
public class JsonDotGetFunctionSet implements FunctionSet {
    @Override
    public Map<String, Function> getFunctions() {
        Map<String, Function> map = new HashMap<String, Function>();
        map.put("json.get", new JsonDotGetFunction(false, false));
        map.put("json.eget", new JsonDotGetFunction(true, false));
        map.put("json.gete", new JsonDotGetFunction(false, true));
        map.put("json.egete", new JsonDotGetFunction(true, true));
        return map;
    }

    private static class JsonDotGetFunction extends Function {
        private boolean encodedInput;
        private boolean encodedOutput;

        public JsonDotGetFunction(boolean encodedInput, boolean encodedOutput) {
            this.encodedInput = encodedInput;
            this.encodedOutput = encodedOutput;
        }

        @Override
        public void evaluate(Sink sink, ArgumentList arguments, FactContext context) {
            JSON.Nested nested = new JSON.Nested(arguments.resolveString(0), false);
            nested.down(arguments.subList(1), encodedInput);
            sink.write(nested.get(encodedOutput));
        }

        @Override
        public String getHelp(String topic) {
            return "Syntax: {json.[e]get[e]|<json>|<key1>|<key2>|...} -- Look up the value at the specified path in " +
                    "the specified JSON data. TODO: Need more documentation";
        }
    }
}
