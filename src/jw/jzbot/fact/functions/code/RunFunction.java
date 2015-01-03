package jw.jzbot.fact.functions.code;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import jw.jzbot.fact.ast.FactEntity;
import jw.jzbot.fact.ast.Sequence;

public class RunFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        String name = arguments.resolveString(0);
        Function function = context.getLocalFunctions().get(name);
        function.evaluate(sink, new ArgumentList(new Sequence(), context), context);
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {run|<name>} -- Runs the stored subroutine with the specified " +
        		"name that has previously been stored with {store}.";
    }
    
}
