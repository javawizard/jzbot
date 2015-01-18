package jw.jzbot.fact.functions.json;

import jw.jzbot.fact.*;
import jw.jzbot.fact.ast.FunctionReference;
import jw.jzbot.fact.ast.Literal;
import jw.jzbot.fact.ast.Sequence;
import jw.jzbot.fact.output.StringSink;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by aboyd on 2015-01-18.
 */
public class JsonDotModFunctionSet implements FunctionSet {
    @Override
    public Map<String, Function> getFunctions() {
        Map<String, Function> map = new HashMap<String, Function>();
        map.put("json.mod", new JsonDotModFunction(false, false));
        map.put("json.emod", new JsonDotModFunction(true, false));
        map.put("json.mode", new JsonDotModFunction(false, true));
        map.put("json.emode", new JsonDotModFunction(true, true));
        return map;
    }

    private static class JsonDotModFunction extends Function {
        private boolean encodedKeys;
        private boolean encodedValue;

        public JsonDotModFunction(boolean encodedKeys, boolean encodedValue) {
            this.encodedKeys = encodedKeys;
            this.encodedValue = encodedValue;
        }

        @Override
        public void evaluate(Sink sink, ArgumentList arguments, FactContext context) {
            JSON.Nested nested = new JSON.Nested(arguments.resolveString(0), true);
            int numberOfKeys = Integer.parseInt(arguments.resolveString(1));
            nested.down(arguments.subList(2, numberOfKeys), encodedKeys);
            String current = nested.get(encodedValue, false);

            StringSink result = new StringSink();
            Sequence sequence = new Sequence();
            sequence.add(arguments.getEntity(2 + numberOfKeys));
            sequence.add(new Literal(current));

            arguments = arguments.subList(3 + numberOfKeys);
            for (int i = 0; i < arguments.length(); i++)
                sequence.add(arguments.getEntity(i));
            FunctionReference function = new FunctionReference(sequence);
            function.resolve(result, context);

            nested.set(result.getCurrentValue(), encodedValue);
            sink.write(nested.encodedToplevel());
        }

        @Override
        public String getHelp(String topic) {
            return "Syntax: {json.[e]mod[e]|<json>|N|<key1>|...|<keyN>|<function>|<arg2>|...} -- TBD";
        }
    }
}
