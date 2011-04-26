package jw.jzbot.fact.functions.map;

import java.util.HashMap;

import net.sf.opengroove.common.utils.StringUtils;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class MparseFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        String[] items = arguments.resolveString(2).split(arguments.resolveString(0));
        String kv = arguments.resolveString(1);
        HashMap<String, String> map = new HashMap<String, String>();
        for (String item : items)
        {
            String[] tokens = StringUtils.split(item, kv, 2);
            map.put(tokens[0], tokens[1]);
        }
        sink.write(MapUtils.encode(map));
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {mparse|<e>|<kv>|<value>} -- Splits <value> around "
            + "matches of the regex <e>, then puts these values together "
            + "into a new map, with everything before <kv> in each resulting "
            + "item constituting the key and everything after <kv> "
            + "constituting the value.";
    }
    
}
