package jw.jzbot.fact.functions;

import jw.jzbot.JZBot;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class DatasizeFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        sink.write(JZBot.datasize(Long.parseLong(arguments.getString(0))));
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{datasize||<size>}} -- Formats <size>, which is a size in "
                + "bytes, into a more human-readable format. For example, "
                + "{{datasize||12345}} would result in \"12.1KB\".";
    }
    
}
