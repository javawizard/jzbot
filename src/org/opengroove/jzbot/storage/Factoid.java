package org.opengroove.jzbot.storage;

import net.sf.opengroove.common.proxystorage.Property;
import net.sf.opengroove.common.proxystorage.ProxyBean;

@ProxyBean
public interface Factoid
{
    @Property
    public String getName();
    
    public void setName(String name);
    
    @Property
    public boolean isActive();
    
    public void setActive(boolean active);
    
    @Property
    public String getValue();
    
    public void setValue(String value);
    
    /**
     * Gets the hostname of the creator of this factoid. Factoids can be deleted
     * only by their creator and by ops (for channel-specific factoids) and
     * superops.
     * 
     * @return The hostname of the creator of this factoid
     */
    @Property
    public String getCreator();
    
    public void setCreator(String creator);
}
