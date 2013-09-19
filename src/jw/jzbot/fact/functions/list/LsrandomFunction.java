package jw.jzbot.fact.functions.list;

import java.util.List;
import java.util.Random;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import jw.jzbot.fact.utils.list.ListUtils;

public class LsrandomFunction extends Function {
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context) {
        List<String> list = ListUtils.parseList(arguments.resolveString(0));
        if (list.size() > 0)
            sink.write(list.get((int) (Math.random() * list.size())));
    }
    
    @Override
    public String getHelp(String topic) {
        return "Syntax: {lsrandom|<list>} -- Returns a randomly-selected item from the specified list.";
    }
    
}
