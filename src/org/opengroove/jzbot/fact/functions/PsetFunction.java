package org.opengroove.jzbot.fact.functions;

import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.Function;

public class PsetFunction extends Function
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
        return "Syntax: {{pset||<name>||<value>}} -- Sets the specified persistent variable to "
                + "the specified value. Persistent variables are different from global variables "
                + "in that global variables are lost whenever the bot is restarted, whereas "
                + "persistent variables are stored in the database and so are not lost when "
                + "the bot restarts.";
    }
    
    @Override
    public String getName()
    {
        return "pset";
    }
    
}
