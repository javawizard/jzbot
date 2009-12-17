package jw.jzbot;

/**
 * A wrapper around a Connection. Almost all of JZBot accesses connection objects through
 * a ConnectionWrapper. A connection wrapper primarily takes care of logging events sent
 * to the connection. Events received from a connection, however, are logged by the
 * connection context instead of by the wrapper.
 * 
 * @author Alexander Boyd
 * 
 */
public class ConnectionWrapper
{
    private ConnectionContext context;
    
    public ConnectionWrapper(ConnectionContext context)
    {
        this.context = context;
    }
    
    public ConnectionContext getContext()
    {
        return context;
    }
}
