package jw.jzbot.plugins;

import jw.jzbot.storage.Node;
import jw.jzbot.storage.PluginStorage;

public class PluginContext
{
    public final Plugin plugin;
    
    public final Node storage;
    
    private PluginStorage pluginStorage;
    
    public PluginContext(Plugin plugin)
    {
        super();
        this.plugin = plugin;
        this.pluginStorage = PluginSystem.getStorage(plugin.info.name);
        this.storage = pluginStorage.getNode();
    }
    
    /**
     * Calls log(false, message)
     * 
     * @param message
     */
    public void log(String message)
    {
        log(false, message);
    }
    
    /**
     * Logs a message about this plugin. If <tt>error</tt> is <tt>true</tt>, the error
     * will be reported to the bot's primary channel.
     * 
     * @param error
     * @param message
     */
    public void log(boolean error, String message)
    {
        PluginSystem.log(plugin.info.name, error, message);
    }
    
    /**
     * Calls log(true, message)
     * 
     * @param message
     */
    public void error(String message)
    {
        log(true, message);
    }
}
