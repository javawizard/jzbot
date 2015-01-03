package jw.jzbot.fact.functions.code;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

/**
 * Created by aboyd on 2015-01-02.
 */
public class AliasFunction extends Function {
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context) {
        String currentName = arguments.resolveString(0);
        String newName = arguments.resolveString(1);
        context.getLocalFunctions().put(newName, context.getFunction(currentName, null));
    }

    @Override
    public String getHelp(String topic) {
        return "TBD";
    }
}
