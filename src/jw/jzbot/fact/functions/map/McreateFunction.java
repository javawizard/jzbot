package jw.jzbot.fact.functions.map;

import java.util.HashMap;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class McreateFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        sink.write(MapUtils.encode(new HashMap<String, String>()));
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {mcreate|<key1>|<value1>|...} -- Creates a new "
            + "associative array initialized with the specified keys and "
            + "values. If no arguments are specified, an empty map is "
            + "created. This map can then be manipulated with {mget}, {mset}, "
            + "{mlistkeys}, {munset}, etc.";
    }
    
}
