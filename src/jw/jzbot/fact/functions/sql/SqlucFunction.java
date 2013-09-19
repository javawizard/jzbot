package jw.jzbot.fact.functions.sql;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class SqlucFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        SqlupdateFunction.doUpdate(sink, arguments, context, true);
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {sqluc|<statement>|<arg1>|...} -- Exactly the same as "
            + "{sqlupdate|<statement>|<arg1>|...}, but this function evaluates "
            + "to the number of rows that were affected as a result of the statement "
            + "being executed.";
    }
    
}
