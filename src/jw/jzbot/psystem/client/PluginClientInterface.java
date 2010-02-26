package jw.jzbot.psystem.client;

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
    
}
