package jw.jzbot.fact.functions.list;

import java.util.Arrays;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import jw.jzbot.fact.utils.list.ListUtils;

public class LsfixedFunction extends Function {
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context) {
        sink.write(ListUtils.formatList(Arrays.asList(arguments.evalToArray())));
    }
    
    @Override
    public String getHelp(String topic) {
        return "Syntax: {lsfixed|<item1>|<item2>|...} -- Returns a list containing the specified items."
                + "The result of {lsfixed} without any arguments is an empty list.";
    }
    
}
