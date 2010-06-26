package jw.jzbot.fact.functions.vars;

import jw.jzbot.JZBot;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import jw.jzbot.fact.functions.conditional.IfFunction;

public class LaddFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        String var = arguments.resolveString(0);
        String equation = arguments.resolveString(1);
        String result = JZBot.evaluateEquation(equation, context.getChannel());
        String oldValue = context.getLocalVars().get(var);
        if (oldValue == null || oldValue.equals(""))
            oldValue = "0";
        String newValue =
                JZBot.evaluateEquation(oldValue + "+" + result, context.getChannel());
        context.getLocalVars().put(var, newValue);
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {ladd|<var>|<equation>} -- Computes <equation> as if it were "
            + "passed to the {eval} function, then adds the value to the numerical "
            + "value currently stored in the local variable <var>. If <var> does "
            + "not exist, this function acts as if its current value is 0.";
    }
    
}
