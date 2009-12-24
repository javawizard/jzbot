package jw.jzbot.fact.functions;

import java.util.HashMap;
import java.util.Map;

import jw.jzbot.JZBot;
import jw.jzbot.JZBot.ImportLevel;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class CascadeFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        context.incrementImportCount();
        Map<String, String> map = new HashMap<String, String>();
        String regex = arguments.getString(0);
        arguments = arguments.subList(1);
        for (String s : context.getLocalVars().keySet())
        {
            if (s.matches(regex))
                map.put(s, context.getLocalVars().get(s));
        }
        // FIXME: re-work the import functionality to write to the main sink instead of
        // writing to a string sink and then copying into this sink
        sink.write(JZBot.doFactImport(context.getServer(), context.getChannel(), arguments,
                context.getSender(), context.getSource(), true, context.getQuota(),
                ImportLevel.any, map));
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
