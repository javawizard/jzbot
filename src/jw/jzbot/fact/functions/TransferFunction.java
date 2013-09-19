package jw.jzbot.fact.functions;

import java.util.HashMap;
import java.util.Map;

import jw.jzbot.JZBot;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.Deferred;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import jw.jzbot.storage.MapEntry;

public class TransferFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        Map<String, String> newMap = new HashMap<String, String>();
        String from = arguments.resolveString(0);
        String to = arguments.resolveString(1);
        String regex = arguments.resolveString(2);
        String varname = null;
        Deferred namegen = null;
        if (arguments.length() > 4)
        {
            varname = arguments.resolveString(3);
            namegen = arguments.getDeferred(4);
        }
        from = from.substring(0, 1).toLowerCase();
        to = to.substring(0, 1).toLowerCase();
        /*
         * First step is getting the variables and the values that we're going to move to
         * the new variable type.
         */
        if (from.equals("p"))
        {
            for (MapEntry entry : JZBot.storage.getPersistentVariables().isolate())
            {
                String key = entry.getKey();
                if (key.matches(regex))
                    newMap.put(key, entry.getValue());
            }
        }
        else
        {
            assert from.equals("g") || from.equals("l") || from.equals("c") : "From "
                    + "(substring 0,1) was " + from;
            Map<String, String> source = from.equals("g") ? context.getGlobalVars() : from
                    .equals("l") ? context.getLocalVars() : context.getChainVars();
            for (Map.Entry<String, String> entry : source.entrySet())
            {
                if (entry.getKey().matches(regex))
                    newMap.put(entry.getKey(), entry.getValue());
            }
        }
        /*
         * Second step is running the namegen stuff.
         */
        Map<String, String> namedMap;
        if (varname != null)
        {
            namedMap = new HashMap<String, String>();
            String previousValue = context.getLocalVars().get(varname);
            for (Map.Entry<String, String> entry : newMap.entrySet())
            {
                context.getLocalVars().put(varname, entry.getKey());
                String newName = namegen.resolveString();
                if (!newName.equals(""))
                    namedMap.put(newName, entry.getValue());
            }
            if (previousValue == null)
                context.getLocalVars().remove(varname);
            else
                context.getLocalVars().put(varname, previousValue);
        }
        else
        {
            namedMap = newMap;
        }
        /*
         * Third (and last) step is storing the new vars.
         */
        if (to.equals("p"))
        {
            for (Map.Entry<String, String> toAdd : namedMap.entrySet())
            {
                MapEntry entry = JZBot.storage.getPersistentVariable(toAdd.getKey());
                if (entry == null)
                {
                    entry = JZBot.storage.createMapEntry();
                    entry.setKey(toAdd.getKey());
                    JZBot.storage.getPersistentVariables().add(entry);
                }
                entry.setValue(toAdd.getValue());
            }
        }
        else
        {
            assert to.equals("g") || to.equals("l") || to.equals("c") : "To "
                    + "(substring 0,1) was " + to;
            Map<String, String> target = to.equals("g") ? context.getGlobalVars() : to
                    .equals("l") ? context.getLocalVars() : context.getChainVars();
            target.putAll(namedMap);
        }
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {transfer|<from>|<to>|<regex>|<varname>|<namegen>} -- "
                + "Transfers a set of variables from one variable type to another variable "
                + "type, or bulk-renames a set of variables. <from> and <to> are one of "
                + "\"global\", \"local\", \"persistent\", or \"chain\", or the first "
                + "letter of one of those. They specify the type of variable to copy \n"
                + "from and the type of variable to copy to. All variables whose names "
                + "match <regex> will be copied. <varname> and <namegen> are optional, "
                + "and if present, for each variable copied, a local variable named "
                + "<varname> will be set to the name of the variable to copy, and "
                + "<namegen> will be run. <namegen> should then evaluate to the new \n"
                + "name for the variable. If <varname> and <namegen> are absent, the "
                + "variable's original name is kept. If <namegen> evaluates to the "
                + "empty string, the variable is not copied.";
    }
    
}
