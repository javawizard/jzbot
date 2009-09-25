package org.opengroove.jzbot.fact.functions;

import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.Function;

public class WeatherFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{weather||<zipcode>||<prefix>}} -- Gets information on the current " +
        		"weather conditions for <zipcode> by contacting the WeatherBug and Yahoo " +
        		"servers. Weather information is then placed in a number of different local " +
        		"variables, each prefixed with <prefix> to avoid conflicting variable names. " +
        		"<prefix> is optional, ";
    }
    
}
