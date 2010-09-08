package jw.jzbot.plugins;

import java.io.File;

import jw.jzbot.storage.Node;
import jw.jzbot.storage.PluginStorage;

public class PluginContext
{
    public final Plugin plugin;
    
    public final Node storage;
    
    public final File storageFolder;
    
    private PluginStorage pluginStorage;
    
    public PluginContext(Plugin plugin)
    {
        super();
        this.plugin = plugin;
        this.pluginStorage = PluginSystem.getStorage(plugin.info.name);
        this.storage = pluginStorage.getNode();
        this.storageFolder = new File("storage/plugin-storage/" + plugin.info.name);
        if (!this.storageFolder.exists())
            this.storageFolder.mkdirs();
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
