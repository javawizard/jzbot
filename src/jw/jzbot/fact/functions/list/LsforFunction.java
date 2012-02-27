package jw.jzbot.fact.functions.list;

import java.util.ArrayList;
import java.util.List;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.Deferred;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import jw.jzbot.fact.exceptions.BreakException;
import jw.jzbot.fact.exceptions.ContinueException;
import jw.jzbot.fact.exceptions.NestedLoopException;
import jw.jzbot.fact.output.DelimitedSink;
import jw.jzbot.fact.output.StringSink;
import jw.jzbot.fact.utils.list.ListUtils;

public class LsforFunction extends Function {
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context) {
        List<String> list = ListUtils.parseList(arguments.resolveString(0));
        String varname = arguments.resolveString(1);
        String indexVarname = null;
        Deferred action;
        if (arguments.length() == 3) {
            action = arguments.getDeferred(2);
        } else {
            indexVarname = arguments.resolveString(2);
            action = arguments.getDeferred(3);
        }
        String previousValue = context.getLocalVars().get(varname);
        String previousIndexValue = null;
        if (indexVarname != null)
            previousIndexValue = context.getLocalVars().get(indexVarname);
        List<String> result = new ArrayList<String>();
        for (int i = 0; i < list.size(); i++) {
            String s = list.get(i);
            context.getLocalVars().put(varname, s);
            if (indexVarname != null)
                context.getLocalVars().put(indexVarname, "" + i);
            StringSink thisItemSink = new StringSink();
            try {
                action.resolve(thisItemSink);
            } catch (NestedLoopException e) {
                e.level--;
                if (e.level == -1) {
                    if (e instanceof ContinueException) {
                        result.add(thisItemSink.getCurrentValue());
                        continue;
                    } else if (e instanceof BreakException) {
                        result.add(thisItemSink.getCurrentValue());
                        break;
                    }
                } else {
                    throw e;
                }
            }
            result.add(thisItemSink.getCurrentValue());
        }
        if (previousValue == null)
            context.getLocalVars().remove(varname);
        else
            context.getLocalVars().put(varname, previousValue);
        if (indexVarname != null) {
            if (previousIndexValue == null)
                context.getLocalVars().remove(indexVarname);
            else
                context.getLocalVars().put(indexVarname, previousIndexValue);
        }
        sink.write(ListUtils.formatList(result));
    }
    
    @Override
    public String getHelp(String topic) {
        return "Syntax: {lsfor|<list>|<var>|<action>} or {lsfor|<list>|<var>|<index-var>|<action>} -- "
                + "Runs through each item in the list <list>. For each of "
                + "these items, sets the local variable named <var> to the current item and the local "
                + "variable named <index-var> (if this argument is specified) to the index of the current "
                + "item (indexes start at 0), and then runs <action>. {lsfor} then returns a new list "
                + "containing the results of all of the times <action> was run. (For those of you familiar "
                + "with languages such as Python and Haskell, this function is a list comprehension.)";
    }
    
    public String getName() {
        return "split";
    }
    
}
