package jw.jzbot.storage;

import net.sf.opengroove.common.proxystorage.Default;
import net.sf.opengroove.common.proxystorage.Length;
import net.sf.opengroove.common.proxystorage.Property;
import net.sf.opengroove.common.proxystorage.ProxyBean;

@ProxyBean
public interface MapEntry
{
    @Property
    @Length(4096)
    public String getKey();
    
    public void setKey(String key);
    
    @Property
    @Default(stringValue = "")
    @Length(600000)
    public String getValue();
    
    public void setValue(String value);
}
