package jw.jzbot;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PublicDatabaseUtils
{
    public static ResultSet metadata_tables(java.sql.Connection connection)
            throws SQLException
    {
        return connection.getMetaData().getTables(null, null, null, null);
    }
    
    public static ResultSet metadata_columns(java.sql.Connection connection, String table)
            throws SQLException
    {
        return connection.getMetaData().getColumns(null, null, table, null);
    }
    
    public static ResultSet metadata_types(java.sql.Connection connection)
            throws SQLException
    {
        return connection.getMetaData().getTypeInfo();
    }
}
