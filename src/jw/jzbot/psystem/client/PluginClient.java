package jw.jzbot.psystem.client;

/**
 * A class that manages communication from a plugin to JZBot itself and takes care of
 * authentication, setting up the RPC link, and so on. A minimalistic plugin in Java could
 * essentially contain this in its <tt>public static void main</tt> method:<br/>
 * 
 * <pre>
 * if(args[0].equals(&quot;info&quot;)){
 *     System.out.println(&quot;This is an example plugin.&quot;);
 *     return;
 * }
 * PluginClientInterface pluginInterface = ...;
 * PluginClient client = new PluginClient(args[0], Integer.parseInt(
 *     args[1]), args[2], pluginInterface, null);
 * client.getServerInterface().sendMessage(&quot;freenode&quot;, &quot;#jzbot&quot;, &quot;Hey everyone!&quot;);
 * client.getServerInterface().sendAction(&quot;freenode&quot;, &quot;#jzbot&quot;, &quot;thinks this is cool&quot;);
 * </pre>
 * 
 * <tt>pluginInterface</tt> can generally be a skeleton implementation of
 * {@link PluginClientInterface}. In other words, it can usually just contain empty
 * methods and everything will work ok, with the one exception that the <tt>unload()</tt>
 * method must call <tt>client.unload();</tt>. If it doesn't, when the JZBot user tries to
 * unload the plugin, they will get a message after about 20 seconds that the plugin
 * didn't unload on its own and has been terminated.
 * 
 * The plugin as written above will, when loaded, send "Hey everyone!" as a message to the
 * channel #jzbot at the server named <tt>freenode</tt>. It will then send an action (IE
 * prefixed with "/me") to that same channel.
 * 
 * @author Alexander Boyd
 * 
 */
public class PluginClient
{
    public PluginClient(String host, int port, String key,
            PluginClientInterface clientInterface)
    {
        
    }
}
