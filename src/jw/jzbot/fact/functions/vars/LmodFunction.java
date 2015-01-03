package jw.jzbot.fact.functions.vars;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import jw.jzbot.fact.ast.FactEntity;
import jw.jzbot.fact.ast.FunctionReference;
import jw.jzbot.fact.ast.Literal;
import jw.jzbot.fact.ast.Sequence;
import jw.jzbot.fact.output.StringSink;

/**
 * Created by aboyd on 2015-01-02.
 */
public class LmodFunction extends Function {
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context) {
        String var = arguments.resolveString(0);
        StringSink result = new StringSink();
        Sequence sequence = new Sequence();
        sequence.add(arguments.getEntity(1));
        sequence.add(new Literal(context.getLocalVars().get(var)));
        arguments = arguments.subList(2);
        for (int i = 0; i < arguments.length(); i++)
            sequence.add(arguments.getEntity(i));
        FunctionReference function = new FunctionReference(sequence);
        function.resolve(result, context);
        context.getLocalVars().put(var, result.getCurrentValue());
    }

    @Override
    public String getHelp(String topic) {
        return "Syntax: {lmod|<varname>|<function>|<arg2>|...} -- Passes the current value of " +
                "the specified local variable as the first argument to {<function>}, with <arg2> " +
                "etc. being passed as the remaining arguments, then sets the variable to the " +
                "function's return value. Short for " +
                "{lset|<varname>|{<function>|{lget|<varname}|<arg2>|...}}.";
    }
}
