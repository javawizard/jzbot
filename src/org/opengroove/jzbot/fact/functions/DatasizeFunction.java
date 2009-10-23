package org.opengroove.jzbot.fact.functions;

import org.opengroove.jzbot.JZBot;
import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.Function;

public class DatasizeFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        return JZBot.datasize(Long.parseLong(arguments.get(0)));
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{datasize||<size>}} -- Formats <size>, which is a size in "
                + "bytes, into a more human-readable format. For example, "
                + "{{datasize||12345}} would result in \"12.1KB\".";
    }
    
}
