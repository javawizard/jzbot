package jw.jzbot.storage;

import net.sf.opengroove.common.proxystorage.Property;
import net.sf.opengroove.common.proxystorage.ProxyBean;

@ProxyBean
public interface PersistentKey
{
    @Property
    public String getKey();
    
    public void setKey(String key);
    
    @Property
    public String getName();
    
    public void setName(String name);
}
