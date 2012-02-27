package jw.jzbot.fact.functions.vars;

import java.util.Map;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class _DollarFunction extends Function {
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context) {
        Map<String, String> vars = context.getLocalVars();
        String[] values = arguments.evalToArray();
        if (values.length == 1) {
            if (vars.containsKey(values[0]))
                sink.write(vars.get(values[0]));
        } else {
            vars.put(values[0], values[1]);
        }
    }
    
    @Override
    public String getHelp(String topic) {
        return "Syntax: {$|<var>} or {$|<var>|<value>} -- In the first form, acts exactly like {lget|<var>}. "
                + "In the second form, acts exactly like {lset|<var>|<value>}.";
    }
    
}
