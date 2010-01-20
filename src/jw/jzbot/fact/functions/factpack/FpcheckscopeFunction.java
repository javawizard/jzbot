package jw.jzbot.fact.functions.factpack;

import jw.jzbot.FactScope;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import jw.jzbot.fact.exceptions.FactoidException;
import jw.jzbot.fact.exceptions.FactpackInstallationException;

public class FpcheckscopeFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        String scopeName = arguments.resolveString(0);
        FactScope scope = FactScope.valueOf(scopeName);
        String target = context.getLocalVars().get("factpack-target");
        FactScope actualScope;
        if (target.contains("#"))
            actualScope = FactScope.channel;
        else if (target.contains("@"))
            actualScope = FactScope.server;
        else
            actualScope = FactScope.global;
        if (actualScope != scope)
            throw new FactpackInstallationException("That factpack must be installed at "
                    + scope + " scope, but you tried to install it at " + actualScope
                    + " scope.");
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {fpcheckscope|<level>} -- Ensures that this factpack is "
                + "being installed at exactly the scope level specified. The scope "
                + "level can be either \"global\", \"server\", or \"channel\".";
    }
    
}
