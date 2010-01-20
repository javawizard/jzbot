package jw.jzbot.fact.functions.code;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import jw.jzbot.fact.ast.FactEntity;

public class RunFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        String name = arguments.resolveString(0);
        FactEntity entity = context.getStoredSubroutines().get(name);
        entity.resolve(sink, context);
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {run|<name>} -- Runs the stored subroutine with the specified " +
        		"name that has previously been stored with {store}.";
    }
    
}
