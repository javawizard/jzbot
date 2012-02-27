package jw.jzbot.fact.functions.list;

import java.util.List;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import jw.jzbot.fact.output.DelimitedSink;
import jw.jzbot.fact.utils.list.ListUtils;

public class LsdelimitedFunction extends Function {
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context) {
        List<String> list = ListUtils.parseList(arguments.resolveString(0));
        DelimitedSink s = new DelimitedSink(sink, arguments.resolveString(1));
        for (String text : list) {
            s.next();
            s.write(text);
        }
    }
    
    @Override
    public String getHelp(String topic) {
        return "Syntax: {lsdelimited|<list>|<delimiter>} -- Returns the items in the specified list, separated by <delimiter>.";
    }
}
