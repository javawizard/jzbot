package org.opengroove.jzbot.fact.functions;

import java.util.HashMap;
import java.util.Map;

import org.opengroove.jzbot.JZBot;
import org.opengroove.jzbot.JZBot.ImportLevel;
import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.Function;

public class CascadeFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        context.incrementImportCount();
        Map<String, String> map = new HashMap<String, String>();
        String regex = arguments.get(0);
        arguments = arguments.subList(1);
        for (String s : context.getLocalVars().keySet())
        {
            if (s.matches(regex))
                map.put(s, context.getLocalVars().get(s));
        }
        return JZBot.doFactImport(context.getChannel(), arguments, context
                .getSender(), true, context.getQuota(), ImportLevel.any, map);
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{cascade||<regex>||<factoid>||<argument1>||...}} -- Identical to the "
                + "{{import}} function, with one exception: any local variable in this factoid "
                + "whose name matches the regular expression <regex> will be handed down to "
                + "<factoid> when it is run. Note that changes to the variable inside that "
                + "factoid will not prepegate up to this factoid. Also, special local variables "
                + "(such as\n%1% or %self%) can't be overriden with this function, meaning that "
                + "they will be set to whatever they would be had {{import}} been used instead "
                + "of {{cascade}}. As an example, \"{{import||testfact||something}}\" would "
                + "function exactly the same as \"{{lset||1||something}}{{cascade||1||testfact}}\". "
                + "However, using \"{{lset||1||something}}{{cascade||1||testfact||other}}\", "
                + "\"testfact\" would have %1% equal to \"other\", not \"something\".";
    }
}
