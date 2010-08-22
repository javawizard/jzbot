package jw.jzbot.storage;

import net.sf.opengroove.common.proxystorage.Constructor;
import net.sf.opengroove.common.proxystorage.Property;
import net.sf.opengroove.common.proxystorage.ProxyBean;

@ProxyBean
public interface PluginStorage
{
    /*
     * TODO: Make plugin storage available to plugins themselves. Make it possible to
     * clear a plugin's storage independently of disabling it (and disabling it should not
     * clear its storage automatically). When storage is to be cleared, a clear-on-startup
     * flag is set. This flag can be cleared if the user changes their mind. On startup,
     * just before loading plugins, the storage of indicated plugins is cleared. The root
     * plugin storage node is created and has a name that's empty to start with. The name
     * doesn't really matter much in the long run, and the plugin could change it if it
     * wants. The root node is made available via the plugin's context. The plugin's
     * storage is created for any plugin that's enabled right before it's activated.
     * Storage is not deleted until the delete on startup flag is set. This flag causes
     * the entire PluginStorage object, not just the node, to be deleted. There are
     * subcommands of the plugin command named storage, delete, and nodelete. The first
     * one lists all plugins with storage. The second one marks a plugin's storage for
     * deleting, and the third one unmarks it for deleting. Note that a plugin that is
     * enabled will have its storage deleted and then immediately re-created on startup,
     * which would effectively clear it but not remove it from existence.
     */
    

    @Property
    public Node getNode();
    
    public void setNode(Node node);
    
    @Constructor
    public Node createNode();
    
    @Property
    public String getName();
    
    public void setName(String name);
    
    @Property
    public boolean isDeleteOnStartup();
    
    public void setDeleteOnStartup(boolean delete);
}
