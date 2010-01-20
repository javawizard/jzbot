package jw.jzbot.fact.functions.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import jw.jzbot.JZBot;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import jw.jzbot.fact.exceptions.FactoidException;

public class SqlupdateFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        doUpdate(sink, arguments, context, false);
    }
    
    public static void doUpdate(Sink sink, ArgumentList arguments, FactContext context,
            boolean writeUpdateCount)
    {
        PreparedStatement statement = null;
        try
        {
            statement = JZBot.relationalStore.prepareStatement(arguments.resolveString(0));
            int positionalCount = arguments.length() - 1;
            for (int i = 0; i < positionalCount; i++)
            {
                statement.setObject(i + 1, arguments.resolveString(i + 1));
            }
            int updateCount = statement.executeUpdate();
            if(writeUpdateCount)
                sink.write(updateCount);
        }
        catch (SQLException e)
        {
            throw new FactoidException("An SQL error occurred. See below for details.", e);
        }
        finally
        {
            try
            {
                statement.close();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {sqlupdate|<statement>|<arg1>|...} "
            + "-- Runs the specified SQL statement, "
            + "which can technically be any kind of statement including a query, "
            + "but which will generally be an update/insert/delete/create/drop/etc "
            + "as this function does not provide any means of getting at any "
            + "results returned by the statement. If you need to run an update-style "
            + "statement and know how many rows the statement affected, "
            + "you could try the {sqluc} function. <arg1> etc are positional arguments, "
            + "used in the same way as in the {sqlfor} function.";
    }
    
}
