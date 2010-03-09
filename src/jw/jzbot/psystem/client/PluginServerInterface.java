package jw.jzbot.psystem.client;

/**
 * An interface defining all of the methods that a plugin can call. JZBot implements this
 * interface over RPC, and allows the plugin to use it to request information from JZBot.
 * 
 * @author Alexander Boyd
 * 
 */
public interface PluginServerInterface
{
    /**
     * Requests that this plugin be unloaded. This method will return immediately, without
     * waiting for the plugin to unload. Some time thereafter,
     * {@link PluginClientInterface#unload(int, String)} will be called.
     * 
     * @param reason
     *            The reason for this plugin being unloaded. This can be null.
     */
    public void unload(String reason);
    
    /**
     * Unloads the plugin with the specified name. This should not be called for this
     * plugin; if you want to unload this plugin, use {@link #unload(String)} instead.
     * 
     * @param name
     */
    public void unloadPlugin(String name);
    
    /**
     * Returns the name that this plugin was loaded as.
     * 
     * @return
     */
    public String getName();
    
    /**
     * Runs the specified text as a Fact program. This is similar to sending "~exec" in a
     * chat message to the bot.
     * 
     * @param scope
     *            The scope to run the Fact program under. For example, "@freenode#jzbot",
     *            "@freenode", or "". This should not be null; to use global scope, use
     *            the empty string instead.
     * @param user
     *            The nick of the user that this Fact program should be run by. This
     *            doesn't have to correspond to a real user at all; it's simply the value
     *            that %0% will be set to.
     * @param code
     * @return The text that the Fact program evaluated to
     */
    public String exec(String scope, String user, String hostname, String code);
}
