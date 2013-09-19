package jw.jzbot.fact.functions;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class CharCodeSpecial extends Function
{
    private String name;
    private String v;
    private String help;
    
    public CharCodeSpecial(String name, String v, String help)
    {
        this.name = name;
        this.v = v;
        this.help = help;
    }
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        sink.write(v);
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {" + name + "} -- " + help;
    }
    
}
