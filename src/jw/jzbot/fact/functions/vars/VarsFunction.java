package jw.jzbot.fact.functions.vars;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class VarsFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        String type = arguments.resolveString(0);
        String regex = arguments.resolveString(1);
        String keyvarName = arguments.resolveString(2);
        String valuevarName = arguments.resolveString(3);
        String delimiter = arguments.length() > 5 ? arguments.resolveString(5) : "";
        throw new UnsupportedOperationException();
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {vars|<type>|<regex>|<keyvar>|<valuevar>|<action>|<delimiter>}"
            + " -- Iterates over all of the variables of the type specified. <type> "
            + "is the type of variable; only the first letter is examined, and should "
            + "be the same as the letters used in the {l?vars} series of functions. "
            + "<regex> specifies a regex; only variables whose names match that "
            + "regex will be iterated over. <keyvar> and <valuevar> are names of "
            + "local variables that will be set to the name and value of the "
            + "variable being iterated over, respectively, for each iteration. "
            + "<action> is the action to perform for each variable, and <delimiter>, "
            + "if present (it defaults to the empty string if not present), specifies "
            + "some text that will be placed between each of the results of the actions. "
            + "This text is then what this function evaluates to. Note that the function "
            + "makes a snapshot of the variables and their values before it starts "
            + "iterating, so any modifications to the variables from within the "
            + "action will not change iteration in any way. However, setting the value "
            + "of the variable named by <valuevar> will have the effect of setting "
            + "the actual variable being iterated over to that value.";
    }
    
}
