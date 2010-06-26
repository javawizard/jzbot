package jw.jzbot.fact.functions.text;

import java.util.Arrays;
import java.util.regex.Pattern;

import net.sf.opengroove.common.utils.StringUtils;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class ShiftFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        String delimiter = arguments.resolveString(1);
        String regex = Pattern.quote(delimiter);
        String varName = arguments.resolveString(0);
        String[] varValue = context.getLocalVars().get(varName).split(regex);
        sink.write(varValue[0]);
        String newVarValue;
        if (varValue.length == 1)
            newVarValue = "";
        else
            newVarValue =
                    StringUtils.delimited(Arrays.copyOfRange(varValue, 1, varValue.length),
                            delimiter);
        context.getLocalVars().put(varName, newVarValue);
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {shift|<var>|<delimiter>} -- Splits the text in the local "
            + "variable <var> around the literal string <delimiter>, then removes "
            + "the first item. The rest of the items are then reconstructed into "
            + "a <delimiter>-separated string, and this string is set back into "
            + "the local variable. This function then evaluates to the item that "
            + "was originally removed.";
    }
    
}
