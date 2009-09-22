package org.opengroove.jzbot.storage;

import net.sf.opengroove.common.proxystorage.Property;
import net.sf.opengroove.common.proxystorage.ProxyBean;

@ProxyBean
public interface MapEntry
{
    @Property
    public String getKey();
    
    public void setKey(String key);
    
    @Property
    public String getValue();
    
    public void setValue(String value);
}
