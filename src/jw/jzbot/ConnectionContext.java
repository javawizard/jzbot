package jw.jzbot;

import jw.jzbot.storage.Server;

public class ConnectionContext
{
    private Server datastoreServer;
    private Connection connection;
    private String serverName;
    
    public String getServerName()
    {
        return serverName;
    }
    
    public void setServerName(String serverName)
    {
        this.serverName = serverName;
    }
    
    public Connection getConnection()
    {
        return connection;
    }
    
    public void setConnection(Connection connection)
    {
        this.connection = connection;
    }
    
    public Server getDatastoreServer()
    {
        return datastoreServer;
    }
    
    public void setDatastoreServer(Server datastoreServer)
    {
        this.datastoreServer = datastoreServer;
    }
    
    public String getServer()
    {
        return datastoreServer.getServer();
    }
    
    public int getPort()
    {
        return datastoreServer.getPort();
    }
    
    public String getNick()
    {
        return datastoreServer.getNick();
    }
    
    public String getPassword()
    {
        return datastoreServer.getPassword();
    }
    
}
