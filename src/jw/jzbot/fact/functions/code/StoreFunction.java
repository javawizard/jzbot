package jw.jzbot.fact.functions.code;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.FactEntity;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class StoreFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        String name = arguments.resolveString(0);
        FactEntity code = arguments.getEntity(1);
        context.getStoredSubroutines().put(name,code);
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{store||<name>||<code>}} -- Creates a stored subroutine "
                + "local to this factoid called <name>. When {{run||<name>}} is called, "
                + "<code> will be executed. {{run||<name>}} can be called more than "
                + "once, which will cause the code to be run multiple times. The "
                + "subroutine is local to this factoid, which means that when a particular \n"
                + "factoid is run, all of its subroutines must be defined again.";
    }
    
}
