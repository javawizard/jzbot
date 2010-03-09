package jw.jzbot.psystem.server;

import java.net.Socket;

import jw.jzbot.psystem.client.*;

import jw.jzbot.rpc.RPCLink;

public class LoadedPlugin
{
    /**
     * The name of this plugin.
     */
    public String name;
    /**
     * The RPCLink that is currently connected to this plugin.
     */
    public RPCLink<PluginClientInterface> link;
    /**
     * The socket that is currently connected to this plugin.
     */
    public Socket socket;
    /**
     * If this plugin is an internal plugin, this is the plugin's corresponding process.
     * If this plugin is an external plugin, this is null.
     */
    public Process process;
    /**
     * A lock used when unloading. This makes it so that if two different threads try to
     * unload the same plugin at the same time, they won't cause problems both trying to
     * unload it.
     */
    private final Object lock = new Object();
    
    /**
     * Unloads the plugin. This involves calling the plugin's unload method, then waiting
     * a maximum of 20 seconds for the plugin to disconnect from the RPC service. At the
     * end of this period, if the plugin has not disconnected, the RPC link will be
     * forcibly closed.
     */
    public boolean unload(int reason)
    {
        //FIXME: implement
        return false;
    }
}
