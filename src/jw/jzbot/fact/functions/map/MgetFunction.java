package jw.jzbot.fact.functions.map;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class MgetFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        String value =
                MapUtils.decode(arguments.resolveString(0)).get(arguments.resolveString(1));
        if (value == null)
            value = "";
        sink.write(value);
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {mget|<map>|<key>} -- Gets the value of the specified "
            + "key stored in the specified map, which is a map formerly "
            + "created with {mcreate}. If the specified key does not exist, "
            + "the empty string is returned.";
    }
    
}
