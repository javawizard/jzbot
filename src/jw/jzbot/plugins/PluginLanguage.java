package jw.jzbot.plugins;

import java.io.File;

/**
 * Represents a language that a plugin can be written in. Almost all of the plugin loading
 * process is delegated to implementations of this interface. The implementations then
 * handle the actual loading in a manner specific to the language in which the plugin is
 * written.
 * 
 * @author Alexander Boyd
 * 
 */
public interface PluginLanguage
{
    /**
     * Returns the name of this language. The name is used as the name of the folder that
     * plugins written in this language will be placed in. This should generally be all
     * lowercase. For example, this could be "java" or "python".
     * 
     * @return
     */
    public String getName();
    
    /**
     * Returns a list of plugins present in the specified folder. It's up to the language
     * to determine how this works. There doesn't even need to be a one-to-one
     * correspondence between the files or folders in the specified folder and the plugins
     * returned. Note that this should not execute any code present in the plugins; the
     * usual way to collect description information is to read specially-formatted
     * comments present in the plugin file or to read a metadata file present in a
     * particular folder.
     * 
     * @param folder
     *            The folder to search
     * @return A list of plugins present in the specified folder
     */
    public PluginInfo[] listPlugins(File folder);
    
    /**
     * Loads the specified plugin with the specified context. If this method throws an
     * exception, an error will be sent to the plugin error log, but the plugin will still
     * be marked as loaded to prevent it from being repeatedly loaded.
     * 
     * @param plugin
     * @param context
     * @throws Exception
     *             if a problem happens while loading the plugin
     */
    public void loadPlugin(Plugin plugin, PluginContext context) throws Exception;
}
