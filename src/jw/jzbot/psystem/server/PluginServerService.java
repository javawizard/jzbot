package jw.jzbot.psystem.server;

import jw.jzbot.psystem.client.PluginServerInterface;

/**
 * The JZBot server implementation of PluginServerInterface. This actually implements all
 * of the methods plugins can call.
 * 
 * @author Alexander Boyd
 * 
 */
public class PluginServerService implements PluginServerInterface
{
    private LoadedPlugin plugin;
    
    public PluginServerService(LoadedPlugin plugin)
    {
        this.plugin = plugin;
    }
    
    @Override
    public String exec(String scope, String user, String hostname, String code)
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public String getName()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public void unload(String reason)
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void unloadPlugin(String name)
    {
        // TODO Auto-generated method stub
        
    }
    
}
