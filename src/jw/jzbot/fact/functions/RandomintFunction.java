package jw.jzbot.fact.functions;

import java.util.Random;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;


public class RandomintFunction extends Function
{
    private static Random random = new Random();
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        return "" + random.nextInt(Integer.parseInt(arguments.get(0)));
    }
    
    public String getName()
    {
        return "randomint";
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{randomint||<number>}} -- Returns a number between 0, inclusive, and "
                + "<number>, exclusive, chosen at random. The number will always be a whole "
                + "integer. If you want a number between 1 and <number>, inclusive, you could "
                + "use {{eval||{{random||<number>}}+1}} to do that.";
    }
    
}
