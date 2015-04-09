package jw.jzbot.fact.functions.code;

import jw.jzbot.fact.*;
import jw.jzbot.fact.ast.FactEntity;

public class StoreFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        String name = arguments.resolveString(0);
        FactEntity code = arguments.getEntity(1);
        // TODO: Should we be using latestCodeRunVersion or the context's initial version? Kinda thinking the former
        // in case any functions it invoked returned code or other stuff that it somehow took into consideration, but
        // that's treading on thin ice as it is anyway. At any rate, the former only makes us perhaps needlessly more
        // secure than the latter, so not a huge deal.
        context.getLocalFunctions().put(name, new DynamicFunction(name, context.getLatestCodeRunVersion(), code));
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {store|<name>|<code>} -- Creates a stored subroutine "
            + "local to this factoid called <name>. When {run|<name>} is called, "
            + "<code> will be executed. {run|<name>} can be called more than "
            + "once, which will cause the code to be run multiple times. The "
            + "subroutine is local to this factoid, which means that when a particular "
            + "factoid is run, all of its subroutines must be defined again. As "
            + "an example, \"{store|test|{lget|something}}{lset|something|hi}"
            + "{run|test} {lset|something|bye}{run|test}\" is functionally "
            + "identical to \"{lset|something|hi}{lget|something} {lset|something|"
            + "bye}{lget|something}\", and prints out \"hi bye\".";
    }
    
}
