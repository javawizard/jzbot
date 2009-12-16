package jw.jzbot;

import jw.jzbot.storage.Server;

public class ConnectionContext
{
    private Server datastoreServer;
    
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
