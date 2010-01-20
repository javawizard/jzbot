package jw.jzbot.fact.functions;

import java.math.BigDecimal;
import java.text.DecimalFormatSymbols;

import jw.jzbot.JZBot;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import jw.jzbot.fact.exceptions.FactoidException;

import net.sf.opengroove.common.utils.StringUtils;

import org.cheffo.jeplite.JEP;

public class EvalFunction extends Function
{
    public static DecimalFormatSymbols symbols = new DecimalFormatSymbols();
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        if (arguments.length() == 1)
            sink.write(JZBot.evaluateEquation(arguments.resolveString(0), context
                    .getChannel()));
        else
            sink.write(JZBot.evaluateEquation(arguments.resolveString(1), context
                    .getChannel(), arguments.resolveString(0)));
    }
    
    public String getName()
    {
        return "eval";
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {eval|<toeval>} or {eval|<engine>|<toeval>} -- "
                + "Evaluates <toeval> as a mathematical equation. Engine specifies "
                + "the engine to use. Each engine exhibits different properties "
                + "and equation syntax.\n"
                + "Allowed engines are (separated by a space): "
                + StringUtils.delimited(JZBot.evalEngines.keySet().toArray(new String[0]),
                        " ");
    }
}
