package jw.jzbot.storage;

import net.sf.opengroove.common.proxystorage.Default;
import net.sf.opengroove.common.proxystorage.Length;
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
    public boolean isRestricted();
    
    public void setRestricted(boolean restricted);
    
    @Property
    public boolean isLibrary();
    
    public void setLibrary(boolean library);
    
    @Property
    @Length(97000)
    public String getValue();
    
    public void setValue(String value);
    
    /**
     * Gets the hostname of the creator of this factoid. Factoids can be deleted only by
     * their creator and by ops (for channel-specific factoids) and superops.
     * 
     * @return The hostname of the creator of this factoid
     */
    @Property
    public String getCreator();
    
    public void setCreator(String creator);
    
    @Property
    @Default(stringValue = "(unknown)")
    public String getCreatorUsername();
    
    public void setCreatorUsername(String creatorUsername);
    
    @Property
    @Default(stringValue = "")
    public String getCreatorNick();
    
    public void setCreatorNick(String nick);
    
    @Property
    public long getCreationTime();
    
    public void setCreationTime(long time);
    
    @Property
    public int getDirectRequests();
    
    public void setDirectRequests(int count);
    
    @Property
    public int getIndirectRequests();
    
    public void setIndirectRequests(int requests);
    
    /**
     * Gets the factpack for this factoid. The factpack is in the format of
     * "<scope>:<name>", where <scope> is either the empty string for global factpacks or
     * the name of a channel for channel-specific factpacks, and <name> is the canonical
     * name of the factpack that caused this factoid to be created.
     * 
     * @return
     */
    @Property
    public String getFactpack();
    
    public void setFactpack(String factpack);
    
    /**
     * True if this factoid is an uninstall factoid for its factpack. Uninstall factoids
     * will be run after a factpack has been uninstalled.
     * 
     * @return
     */
    @Property
    public boolean isUninstall();
    
    public void setUninstall(boolean uninstall);
    
    @Property
    public String getAttribution();
    
    public void setAttribution(String attribution);
}
