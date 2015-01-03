package jw.jzbot.fact.functions.code;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

/**
 * Created by aboyd on 2014-12-23.
 */
public class ArgcountFunction extends Function {
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context) {
        sink.write("" + context.getFunctionArguments().length());
    }

    @Override
    public String getHelp(String topic) {
        return "Syntax: {argcount} -- Return the number of arguments passed to this user-defined function. If this " +
                "is invoked from anything other than a user-defined function, the results should be considered to be " +
                "undefined - the current behavior is to return 0, but I may change this function to return the empty " +
                "string in such a case.";
    }
}
