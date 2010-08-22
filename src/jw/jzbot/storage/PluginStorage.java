package jw.jzbot.storage;

import net.sf.opengroove.common.proxystorage.Constructor;
import net.sf.opengroove.common.proxystorage.Property;
import net.sf.opengroove.common.proxystorage.ProxyBean;

@ProxyBean
public interface PluginStorage
{
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
