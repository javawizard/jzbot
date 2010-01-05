package jw.jzbot.fact.functions.factpack;

import jw.jzbot.FactScope;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class FpcheckscopeFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        String scopeName = arguments.resolveString(0);
        // FactScope scope = FactScope.
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {fpcheckscope|<level>} -- Ensures that this factpack is "
                + "being installed at exactly the scope level specified. The scope "
                + "level can be either \"global\", \"server\", or \"channel\".";
    }
    
}
