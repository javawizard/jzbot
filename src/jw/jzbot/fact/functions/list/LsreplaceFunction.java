package jw.jzbot.fact.functions.list;

import java.util.List;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import jw.jzbot.fact.utils.list.ListUtils;

public class LsreplaceFunction extends Function {
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context) {
        List<String> list = ListUtils.parseList(arguments.resolveString(0));
        list.set(Integer.parseInt(arguments.resolveString(1)),
                arguments.resolveString(2));
        sink.write(ListUtils.formatList(list));
    }
    
    @Override
    public String getHelp(String topic) {
        return "Syntax: {lsreplace|<list>|<index>|<item>} -- Returns a list created by replacing the item at <index> with <item>.";
    }
    
}
