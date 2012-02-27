package jw.jzbot.fact.functions.list;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import jw.jzbot.fact.exceptions.BreakException;
import jw.jzbot.fact.exceptions.ContinueException;
import jw.jzbot.fact.exceptions.NestedLoopException;
import jw.jzbot.fact.output.DelimitedSink;

public class LsforFunction extends Function {
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context) {
        String regex = arguments.resolveString(0);
        String string = arguments.resolveString(1);
        String varname = arguments.resolveString(2);
        String delimiter = "";
        if (arguments.length() > 4)
            delimiter = arguments.resolveString(4);
        String[] split = string.split(regex);
        if (split.length == 1 && split[0].equals(""))
            split = new String[0];
        String previousValue = context.getLocalVars().get(varname);
        DelimitedSink result = new DelimitedSink(sink, delimiter);
        for (String s : split) {
            result.next();
            context.getLocalVars().put(varname, s);
            try {
                arguments.resolve(3, result);
            } catch (NestedLoopException e) {
                e.level--;
                if (e.level == -1) {
                    if (e instanceof ContinueException)
                        continue;
                    else if (e instanceof BreakException)
                        break;
                } else {
                    throw e;
                }
            }
        }
        if (previousValue == null)
            context.getLocalVars().remove(varname);
        else
            context.getLocalVars().put(varname, previousValue);
    }
    
    @Override
    public String getHelp(String topic) {
        return "Syntax: {lsfor|<list>|<var>|<action>} or {lsfor|<list>|<var>|<index-var>|<action>} -- "
                + "Runs through each item in the list <list>. For each of "
                + "these items, sets the local variable named <var> to the current item and the local "
                + "variable named <index-var> (if this argument is specified) to the index of the current " +
                "item (indexes start at 0), and ";
    }
    
    public String getName() {
        return "split";
    }
    
}
