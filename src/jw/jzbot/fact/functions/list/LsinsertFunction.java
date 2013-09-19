package jw.jzbot.fact.functions.list;

import java.util.List;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import jw.jzbot.fact.utils.list.ListUtils;

public class LsinsertFunction extends Function {
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context) {
        List<String> list = ListUtils.parseList(arguments.resolveString(0));
        list.add(Integer.parseInt(arguments.resolveString(1)),
                arguments.resolveString(2));
        sink.write(ListUtils.formatList(list));
    }
    
    @Override
    public String getHelp(String topic) {
        return "Syntax: {lsinsert|<list>|<index>|<item>} -- Returns a list "
                + "created by inserting the specified item at the specified "
                + "position in the specified list.";
    }
    
    public static void templateThisDoesNothing(Sink sink, ArgumentList arguments) {
        List<String> list = ListUtils.parseList(arguments.resolveString(0));
        sink.write(ListUtils.formatList(list));
    }
    
}
