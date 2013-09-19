package jw.jzbot.fact.functions.list;

import java.util.List;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import jw.jzbot.fact.utils.list.ListUtils;

public class LsdeleteitemFunction extends Function {
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context) {
        List<String> list = ListUtils.parseList(arguments.resolveString(0));
        list.remove(arguments.resolveString(1));
        sink.write(ListUtils.formatList(list));
    }
    
    @Override
    public String getHelp(String topic) {
        return "Syntax: {lsdeleteitem|<list>|<item>} -- Returns a list created by "
                + "removing the first occurrence of <item> from the specified list.";
    }
    
}
