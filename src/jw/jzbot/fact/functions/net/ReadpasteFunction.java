package jw.jzbot.fact.functions.net;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import jw.jzbot.pastebin.PastebinService;
import jw.jzbot.utils.Pastebin;

public class ReadpasteFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        sink.write(PastebinService.readPost(arguments.resolveString(0)).getData());
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {readpaste|<pasteurl>} -- Reads a pastebin post made "
                + "at any of the enabled pastebin providers. ";
    }
    
}
