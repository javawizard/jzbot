package jw.jzbot.fact.functions.map;

import java.util.Map;

import net.sf.opengroove.common.utils.StringUtils;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class MlistkeysFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        Map<String, String> map = MapUtils.decode(arguments.resolveString(0));
        sink.write(StringUtils.delimited(map.keySet(), arguments.resolveString(1)));
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {mlistkeys|<map>|<delimiter>} -- Returns a string "
            + "containing the keys present in <map>, separated by <delimiter>.";
    }
    
}
