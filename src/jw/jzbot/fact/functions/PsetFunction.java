package jw.jzbot.fact.functions;

import jw.jzbot.JZBot;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.storage.MapEntry;

public class PsetFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        String key = arguments.get(0);
        String value = arguments.get(1);
        if (key.length() > 256 || value.length() > (80 * 1024))
            throw new RuntimeException(
                    "Key longer than 256 or value longer than " + (80 * 1024));
        MapEntry entry = JZBot.storage.getPersistentVariable(key);
        if (entry == null)
        {
            entry = JZBot.storage.createMapEntry();
            entry.setKey(key);
            JZBot.storage.getPersistentVariables().add(entry);
        }
        entry.setValue(value);
        return "";
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{pset||<varname>||<value>}} -- Sets the specified persistent variable to "
                + "the specified value. Persistent variables are different from global variables "
                + "in that global variables are lost whenever the bot is restarted, whereas "
                + "persistent variables are stored in the database and so are not lost when "
                + "the bot restarts.";
    }
    
    public String getName()
    {
        return "pset";
    }
    
}
