package jw.jzbot.fact.functions.net;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.pastebin.PastebinService;
import jw.jzbot.utils.Pastebin;

public class ReadpasteFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        return PastebinService.readPost(arguments.get(0)).getData();
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{readpaste||<pasteurl>}} -- Reads a pastebin post made "
                + "at any of the enabled pastebin providers. ";
    }
    
}
