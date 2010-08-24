package jw.jzbot.storage;

import net.sf.opengroove.common.proxystorage.Property;
import net.sf.opengroove.common.proxystorage.ProxyBean;

@ProxyBean
public interface ConfigVariable
{
    @Property
    public String getName();
    
    public void setName(String name);
    
    @Property
    public String getValue();
    
    public void setValue(String value);
}
