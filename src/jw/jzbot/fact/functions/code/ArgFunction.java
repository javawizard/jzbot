package jw.jzbot.fact.functions.code;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import jw.jzbot.fact.exceptions.FactoidException;

/**
 * Created by aboyd on 2014-12-23.
 */
public class ArgFunction extends Function {
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context) {
        // 1-based for now, since args to factoids are the same (%1%, %2%, etc.)
        int index = Integer.parseInt(arguments.resolveString(0)) - 1;
        if (index >= context.getFunctionArguments().length())
            throw new FactoidException("Trying to get argument #" + (index + 1) + ", but only " +
                    context.getFunctionArguments().length() + " arguments were passed to this function");
        context.getFunctionArguments().resolve(index, sink);
    }

    @Override
    public String getHelp(String topic) {
        return "TBD";
    }
}
