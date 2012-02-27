package jw.jzbot.fact.functions.list;

import java.util.List;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import jw.jzbot.fact.utils.list.ListUtils;

public class LscontainsFunction extends Function {
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context) {
        List<String> list = ListUtils.parseList(arguments.resolveString(0));
        if (list.contains(arguments.resolveString(0)))
            sink.write(1);
        else
            sink.write(0);
    }
    
    @Override
    public String getHelp(String topic) {
        return "Syntax: {lscontains|<list>|<item>} -- Returns 1 if the specified "
                + "list contains the specified item, 0 if it doesn't.";
    }
}
