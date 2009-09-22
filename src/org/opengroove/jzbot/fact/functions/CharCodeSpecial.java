package org.opengroove.jzbot.fact.functions;

import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.Function;

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
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        return v;
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{" + name + "}} -- " + help;
    }
    
}
