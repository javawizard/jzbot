package jw.jzbot.fact.functions.map;

import java.util.Map;
import java.util.Set;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import jw.jzbot.fact.output.DelimitedSink;

public class MforeachFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        Map<String, String> map = MapUtils.decode(arguments.resolveString(0));
        Set<Map.Entry<String, String>> entries = map.entrySet();
        DelimitedSink newSink = new DelimitedSink(sink, arguments.resolveString(4));
        String keyName = arguments.resolveString(1);
        String valueName = arguments.resolveString(2);
        String oldKeyVar = context.getLocalVars().get(keyName);
        String oldValueVar = context.getLocalVars().get(valueName);
        for (Map.Entry<String, String> entry : entries)
        {
            context.getLocalVars().put(keyName, entry.getKey());
            context.getLocalVars().put(valueName, entry.getValue());
            newSink.next();
            arguments.resolve(3, newSink);
        }
        if(oldKeyVar != null)
            context.getLocalVars().put(keyName, oldKeyVar);
        if(oldValueVar != null)
            context.getLocalVars().put(valueName, oldValueVar);
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {mforeach|<map>|<kn>|<vn>|<action>|<delimiter>} -- "
            + "Iterates over all items in the specified map. For each "
            + "item, the variable named <kn> is set to the key and the "
            + "variable named <vn> is set to the value. <action> is then "
            + "evaluated. The results of <action> are then put together, "
            + "separated by <delimiter>.";
    }
    
}
