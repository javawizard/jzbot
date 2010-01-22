package jw.jzbot.fact.functions.vars;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class LgvarsFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        StringBuffer b = new StringBuffer();
        String delimiter = "|";
        if (arguments.length() == 2)
            delimiter = arguments.resolveString(1);
        for (String s : context.getGlobalVars().keySet())
        {
            if ((arguments.length() == 0) || s.matches(arguments.getString(0)))
                b.append(delimiter).append(s);
        }
        if (b.length() != 0)
            sink.write(b.substring(1));
    }
    
    public String getName()
    {
        return "lgvars";
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {lgvars|<regex>|<delimiter>} -- Returns a <delimiter>-delimited "
            + "list of the names of all currently-existing global variables. Variables "
            + "that have been set to the empty string are included in this list; "
            + "indeed, checkin members of this list is the only reliable way to see "
            + "if a particular variable actually exists, as it's impossible to detect "
            + "with the {get} function if the variable is nonexistent or simply set "
            + "to the empty string. <regex> is optional, but if it's present, only the "
            + "names of variables that match <regex> will be returned. <delimiter> is "
            + "also optional, and defaults to the pipe character (\"|\") if not "
            + "present.";
    }
    
}
