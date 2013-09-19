package jw.jzbot.storage;

import net.sf.opengroove.common.proxystorage.Property;
import net.sf.opengroove.common.proxystorage.ProxyBean;

@ProxyBean
public interface Operator
{
    @Property
    public String getHostname();
    
    public void setHostname(String hostname);
    
}
