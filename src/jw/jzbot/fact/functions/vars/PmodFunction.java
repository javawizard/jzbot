package jw.jzbot.fact.functions.vars;

import jw.jzbot.JZBot;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import jw.jzbot.fact.ast.FunctionReference;
import jw.jzbot.fact.ast.Literal;
import jw.jzbot.fact.ast.Sequence;
import jw.jzbot.fact.output.StringSink;
import jw.jzbot.storage.MapEntry;

/**
 * Created by aboyd on 2015-01-02.
 */
public class PmodFunction extends Function {
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context) {
        String var = arguments.resolveString(0);
        StringSink result = new StringSink();
        Sequence sequence = new Sequence();
        sequence.add(arguments.getEntity(1));

        MapEntry entry = JZBot.storage.getPersistentVariable(var);

        sequence.add(new Literal(entry.getValue()));
        arguments = arguments.subList(2);
        for (int i = 0; i < arguments.length(); i++)
            sequence.add(arguments.getEntity(i));
        FunctionReference function = new FunctionReference(sequence);
        function.resolve(result, context);

        String value = result.getCurrentValue();
        if (value.length() > (80 * 1024))
            throw new RuntimeException("Value longer than " + (80 * 1024));
        entry.setValue(value);
    }

    @Override
    public String getHelp(String topic) {
        return "TBD";
    }
}
