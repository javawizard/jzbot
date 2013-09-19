package jw.jzbot.fact.functions.map;

import java.util.Map;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class MsetFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        Map<String, String> map = MapUtils.decode(arguments.resolveString(0));
        map.put(arguments.resolveString(1), arguments.resolveString(2));
        sink.write(MapUtils.encode(map));
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax {mset|<map>|<key>|<value>} -- Returns a copy of the "
            + "specified map with <key> set to <value>. If <key> already "
            + "exists as an entry in the specified map, it will be replaced "
            + "with <value>; otherwise, a new entry with the specified "
            + "values will be added.";
    }
    
}
