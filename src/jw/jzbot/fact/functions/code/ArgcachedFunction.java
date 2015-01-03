package jw.jzbot.fact.functions.code;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import jw.jzbot.fact.exceptions.FactoidException;

/**
 * Created by aboyd on 2014-12-23.
 */
public class ArgcachedFunction extends Function {
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context) {
        int index = Integer.parseInt(arguments.resolveString(0)) - 1;
        if (index >= context.getFunctionArguments().length())
            throw new FactoidException("Trying to get argument #" + (index + 1) + ", but only " +
                    context.getFunctionArguments().length() + " arguments were passed to this function");
        context.getFunctionArguments().get(index, sink);
    }

    @Override
    public String getHelp(String topic) {
        return "Syntax: {argcached|<index>} -- Same as {arg|<index>}, but caches the result of evaluating the " +
                "argument such that further invocations of {argcached} for the same argument will return the same " +
                "result without reevaluating the argument. Note that calling {arg} after invoking {argcached} will " +
                "still cause the argument to be evaluated a second time.";
    }
}
