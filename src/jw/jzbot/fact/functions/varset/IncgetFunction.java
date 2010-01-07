package jw.jzbot.fact.functions.varset;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class IncgetFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        sink.write(IncrementFunction.modUp(context, arguments.resolveString(0)));
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {incget|<varname>} -- Same as {increment|<varname>}, but "
                + "evaluates to the new value of the variable. This would be the same "
                + "as \"{increment|<varname>}{lget|<varname>}\" or \""
                + "{increment|<varname>}%<varname>%\".";
    }
    
}
