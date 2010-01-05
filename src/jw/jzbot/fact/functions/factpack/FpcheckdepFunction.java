package jw.jzbot.fact.functions.factpack;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.FactParser;
import jw.jzbot.fact.FactpackInstallationException;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import jw.jzbot.fact.StringSink;

public class FpcheckdepFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        StringSink ss = new StringSink();
        FactParser.getFunctionByClass(FphasdepFunction.class).evaluate(ss, arguments,
                context);
        if (ss.toString().equals("0"))
        {
            String target;
            String factpack;
            if (arguments.length() == 1)
            {
                target = context.getLocalVars().get("factpack-target");
                factpack = arguments.getString(0);
            }
            else
            {
                target = arguments.getString(0);
                factpack = arguments.getString(1);
            }
            throw new FactpackInstallationException(
                    "That factpack requires the factpack \"" + factpack
                            + "\" to be installed at \"" + target
                            + "\" (an empty target means it needs to be installed "
                            + "globally), \nbut this factpack is not installed."
                            + "You'll need to install that factpack first. The "
                            + "JZBot Development Team is planning on adding "
                            + "automatic dependency installation in the future.");
        }
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {fpcheckdep|<target>|<factpack>} -- Exact same syntax as "
                + "{fphasdep}, but aborts factpack installation (as if by a call to "
                + "{fpabort}) if the specified dependency is not present. In fact, "
                + "this function actually calls {fphasdep} to see if the "
                + "dependesncy is present.";
    }
    
}
