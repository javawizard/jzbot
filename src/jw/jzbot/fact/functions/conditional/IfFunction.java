package jw.jzbot.fact.functions.conditional;

import jw.jzbot.JZBot;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import jw.jzbot.fact.exceptions.FactoidException;

public class IfFunction extends Function {
    private static final String yesValues = "|y|yes|t|true|1|1.0|";
    private static final String noValues = "|n|no|f|false|0|0.0|";

    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context) {
        for (int i = 0; i < arguments.length() - 1; i += 2) {
            Boolean result = findValueOrNull(arguments.getString(i));
            if (result == null) {
                result = findValue(JZBot.evaluateEquation(arguments.getString(i),
                        context.getChannel()));
            }
            if (result) {
                arguments.resolve(i + 1, sink);
                return;
            }
        }
        if ((arguments.length() % 2) == 1)
            arguments.resolve(arguments.length() - 1, sink);
    }

    public static boolean findValue(String value) {
        String toCompare = "|" + value.toLowerCase() + "|";
        if (yesValues.contains(toCompare))
            return true;
        else if (noValues.contains(toCompare) || value.equals(""))
            return false;
        else
            throw new FactoidException("Value to check was " + toCompare
                    + " when it should have been in " + yesValues + " or "
                    + noValues);
    }

    public static Boolean findValueOrNull(String value) {
        String toCompare = "|" + value.toLowerCase() + "|";
        if (yesValues.contains(toCompare))
            return true;
        else if (noValues.contains(toCompare) || value.equals(""))
            return false;
        else
            return null;
    }

    public String getName() {
        return "if";
    }

    @Override
    public String getHelp(String topic) {
        return "Syntax: {if|<condition>|<action>|...} or "
                + "{if|<condition>|<action>|...|<otherwise>} -- Evalutes to "
                + "the first <action> for which the preceding <condition> is one "
                + "of the \"true\" values, and <otherwise> "
                + "if <condition> is one of the \"false\" values. If <condition> is neither a "
                + "true value nor a false value, it is interpreted as an equation (as if "
                + "it were passed to {eval}), and then compared again.\n"
                + "The true values are y, yes, t, true, 1, and 1.0, and the false values "
                + "are n, no, f, false, 0, and 0.0. These values are case-insensitive. "
                + "<otherwise> is also optional, and defaults to the empty string if only an "
                + "even number of arguments are given.";
    }

}
