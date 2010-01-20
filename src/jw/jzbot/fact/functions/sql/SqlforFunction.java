package jw.jzbot.fact.functions.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import jw.jzbot.JZBot;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import jw.jzbot.fact.exceptions.ContinueException;
import jw.jzbot.fact.exceptions.FactoidException;
import jw.jzbot.fact.exceptions.NestedLoopException;
import jw.jzbot.fact.output.DelimitedSink;

public class SqlforFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        String sql = arguments.resolveString(0);
        String prefix = arguments.resolveString(1);
        // String action = arguments.resolveString(2); <--- Whoever added this was a total
        // idiot (which would probably be me), since we're supposed to resolve it once per
        // row, not once in general
        String delimiter = arguments.resolveString(3);
        PreparedStatement statement = null;
        ResultSet results = null;
        try
        {
            statement = JZBot.relationalStore.prepareStatement(sql);
            int positionalCount = arguments.length() - 4;
            for (int i = 0; i < positionalCount; i++)
            {
                statement.setObject(i + 1, arguments.resolveString(i + 4));
            }
            results = statement.executeQuery();
            results.next();
            ResultSetMetaData md = results.getMetaData();
            String[] colNames = new String[md.getColumnCount()];
            for (int i = 0; i < colNames.length; i++)
                colNames[i] = md.getColumnLabel(i + 1);
            DelimitedSink delimited = new DelimitedSink(sink, delimiter);
            while (results.next())
            {
                Map<String, String> oldVars = new HashMap<String, String>();
                List<String> nullVars = new LinkedList<String>();
                for (String col : colNames)
                {
                    Object value = results.getObject(col);
                    String propName = prefix + "-" + col;
                    String previousValue = context.getLocalVars().get(propName);
                    if (previousValue == null)
                        nullVars.add(propName);
                    else
                        oldVars.put(propName, previousValue);
                    if (value == null)
                        context.getLocalVars().remove(propName);
                    else
                        context.getLocalVars().put(propName, value.toString());
                }
                delimited.next();
                try
                {
                    arguments.resolve(2, delimited);
                }
                catch (NestedLoopException e)
                {
                    e.level--;
                    if (e.level == -1)
                    {
                        if (e instanceof ContinueException)
                            continue;
                        else
                            break;
                    }
                    else
                        throw e;
                }
                finally
                {
                    for (String s : nullVars)
                        context.getLocalVars().remove(s);
                    context.getLocalVars().putAll(oldVars);
                }
            }
        }
        catch (SQLException e)
        {
            throw new FactoidException("An SQL error occurred. See below for details.", e);
        }
        finally
        {
            // Yes, these can throw NPEs. That's why the catch clause catches all
            // exceptions, not just SQLExceptions.
            try
            {
                results.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            try
            {
                statement.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
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
            + "finishes, it evaluates to all of the <action> results separated by "
            + "<delimiter>. {break} in <action> works correctly.";
    }
}
