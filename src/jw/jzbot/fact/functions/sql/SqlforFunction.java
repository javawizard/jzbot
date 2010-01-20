package jw.jzbot.fact.functions.sql;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class SqlforFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {sqlfor|<query>|<prefix>|<action>|<delimiter>|<arg1>|<arg2>|...}"
            + " -- Runs the SQL statement <query>, which must be a query (IE it must "
            + "not be an update). The query can be a prepared statement (IE it can "
            + "include \"?\" where a particular parameter should be; this allows "
            + "for automatic input sanitization), in which case the values of "
            + "positional arguments are given by <arg1>, <arg2>, and so on. Then, "
            + "for each row in the result, a local variable is set for each non-null "
            + "column in the row. The local variable's name is <prefix>-<colname>, where "
            + "<prefix> is the <prefix> argument to the {sqlfor} function and <colname> "
            + "is the name of the column. <action> is then run for this row. Once {sqlfor} "
            + "finishes, all of the output";
    }
}
