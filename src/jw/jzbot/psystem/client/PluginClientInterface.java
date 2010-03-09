package jw.jzbot.psystem.client;

import jw.jzbot.psystem.server.PluginServerService;
import jw.jzbot.rpc.Function;

/**
 * An interface defining all of the methods that JZBot can call on a plugin. Plugins must
 * provide an implementation of this interface, and JZBot will call various methods on it
 * in response to certain events happening. Note that each of the implementing methods in
 * the plugin's implementation must be annotated with {@link Function}.
 * 
 * @author Alexander Boyd
 * 
 */
public interface PluginClientInterface
{
    /**
     * Indicates that the plugin was unloaded due to the plugin calling
     * {@link PluginServerInterface#unload(String)} on itself.
     */
    public static final int UNLOAD_SELF = 1;
    /**
     * Indicates that the plugin was unloaded due to JZBot shutting down or restarting.
     */
    public static final int UNLOAD_SHUTDOWN = 2;
    /**
     * Indicates that the plugin was unloaded due to a superop running
     * "~plugin unload ...".
     */
    public static final int UNLOAD_COMMAND = 3;
    /**
     * Indicates that the plugin was unloaded due to a factoid using the {unloadplugin}
     * function.
     */
    public static final int UNLOAD_FUNCTION = 4;
    /**
     * Indicates that the plugin was unloaded due to another plugin calling
     * {@link PluginServerInterface#unloadPlugin(String)}.
     */
    public static final int UNLOAD_REQUESTED = 5;
    
    /**
     * This is called when the plugin is about to be unloaded. The plugin should promptly
     * shut everything down, disconnect from RPC, and, if the plugin is an internal
     * plugin, exit.
     * 
     * @param reason
     *            The reason that the plugin is being unloaded, which is one of the
     *            UNLOAD_REASON constants.
     * @param message
     *            The reason that the plugin is being unloaded. This is usually a
     *            human-readable string. If <tt>reason</tt> is {@link #UNLOAD_SELF}, this
     *            will be the argument passed to the
     *            {@link PluginServerInterface#unload(String) unload} method called to
     *            unload this plugin.
     */
    public void unload(int reason, String message);
}
