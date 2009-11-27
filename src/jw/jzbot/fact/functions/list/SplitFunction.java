package jw.jzbot.fact.functions.list;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.DelimitedSink;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class SplitFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        String regex = arguments.resolveString(0);
        String string = arguments.resolveString(1);
        String varname = arguments.resolveString(2);
        String delimiter = "";
        if (arguments.length() > 4)
            delimiter = arguments.resolveString(4);
        String[] split = string.split(regex);
        if (split.length == 1 && split[0].equals(""))
            split = new String[0];
        String previousValue = context.getLocalVars().get(varname);
        DelimitedSink result = new DelimitedSink(sink, delimiter);
        for (String s : split)
        {
            result.next();
            context.getLocalVars().put(varname, s);
            arguments.resolve(3, result);
        }
        if (previousValue == null)
            context.getLocalVars().remove(varname);
        else
            context.getLocalVars().put(varname, previousValue);
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{split||<regex>||<string>||<varname>||<action>||<delimiter>}}"
                + " -- Splits <string> into a list of strings around the regular "
                + "expression <regex>, then evaluates <action> once for each item "
                + "in the list of strings, with the local variable <varname> set to "
                + "the current item in the list. {{split}} then evaluates to what "
                + "each of the evaluations of <action> evaluated to, separated by "
                + "<delimiter>.\nAs an example, {{split||\\\\.||first.second.third||"
                + "thevalue||This is the %thevalue%|| -- }} would result in \"This is "
                + "the first -- This is the second -- This is the third\". <delimiter>"
                + " is optional.\n"
                + "Currently, empty values at the end of the list are ignored. This "
                + "means that in the above example, if \"first.second.third\" were replaced "
                + "with \"first.second.third...\", the output would still be the same. {{split}} "
                + "can be used with <string> being the {{numberlist}} function to effectively "
                + "create a for loop. Also, if <string> is empty, <action> is not evaluated.";
    }
    
    public String getName()
    {
        return "split";
    }
    
}
