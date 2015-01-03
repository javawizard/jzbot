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
        return "Syntax: {alias|<oldname>|<newname>} -- Creates an alias <newname> for the function " +
                "<oldname>. {<newname>|...} will thereafter have the same effect as {<oldname>|...}. " +
                "The alias continues pointing to behave the same regardless of whether <oldname> is " +
                "later overwritten, so you can use {alias} to, for example, exchange the names of two " +
                "functions with {alias|foo|temp}{alias|bar|foo}{alias|temp|bar}.";
    }
}
