package jw.jzbot.fact.functions.code;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

/**
 * Created by aboyd on 2014-12-23.
 */
public class AtlevelFunction extends Function {
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context) {
        FactContext contextAtLevel = context.getAncestorAtLevel(arguments.resolveString(0));
        arguments.getEntity(1).resolve(sink, contextAtLevel);
    }

    @Override
    public String getHelp(String topic) {
        return "TBD";
    }
}
