package jw.jzbot.fact.functions.collections;

import jw.jzbot.fact.*;
import jw.jzbot.fact.exceptions.BreakException;
import jw.jzbot.fact.exceptions.ContinueException;
import jw.jzbot.fact.exceptions.NestedLoopException;
import jw.jzbot.fact.output.DelimitedSink;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SplitFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        String regex = arguments.resolveString(0);
        String string = arguments.resolveString(1);
        String varName = arguments.resolveString(2);
        Deferred action = arguments.getDeferred(3);
        Deferred delimiter = null;
        String[] matchVarNames = new String[0];
        if (arguments.length() > 4) {
            matchVarNames = arguments.subListFromTo(4, arguments.length() - 1).evalToArray();
            delimiter = arguments.getDeferred(arguments.length() - 1);
        }

        Matcher m = Pattern.compile(regex).matcher(string);
        int lastAppended = 0;

        LocalVarSavepoint actionSavepoint = new LocalVarSavepoint(context, varName);
        LocalVarSavepoint delimiterSavepoint = new LocalVarSavepoint(context, matchVarNames);

        while (m.find()) {
            if (regex.equals("") && (m.start() == 0 || m.start() == string.length()))
                continue;

            actionSavepoint.save();
            context.getLocalVars().put(varName, string.substring(lastAppended, m.start()));
            action.resolve(sink);
            actionSavepoint.restore();

            delimiterSavepoint.save();
            for (int i = 0; i < matchVarNames.length; i++) {
                System.out.println("Setting " + matchVarNames[i] + " to " + m.group(i + 1));
                context.getLocalVars().put(matchVarNames[i], m.group(i + 1));
            }
            delimiter.resolve(sink);
            delimiterSavepoint.restore();

            lastAppended = m.end();
        }
        if (!string.equals("")) {
            actionSavepoint.save();
            context.getLocalVars().put(varName, string.substring(lastAppended));
            action.resolve(sink);
            actionSavepoint.restore();
        }
    }

    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {split|<regex>|<string>|<varname>|<action>|<delimiter>}"
            + " or {split|<regex>|<string>|<varname>|<action>|<g1>|<g2>|...|<delimiter>} -- "
            + "Splits <string> into a list of strings around the regular "
            + "expression <regex>, then evaluates <action> once for each item "
            + "in the list of strings, with the local variable <varname> set to "
            + "the current item in the list. {split} then evaluates to what "
            + "each of the evaluations of <action> evaluated to, separated by "
            + "<delimiter>. <delimiter> will be invoked with variables named <g1>, <g2>, etc. "
            + "set to the corresponding groups in the corresponding regex match.";
    }
    
    public String getName()
    {
        return "split";
    }
    
}
