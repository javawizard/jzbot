package jw.jzbot.storage;

import net.sf.opengroove.common.proxystorage.Property;
import net.sf.opengroove.common.proxystorage.ProxyBean;

/**
 * Created by aboyd on 2014-12-23.
 */
@ProxyBean
public interface StoredFunction extends ModifiedAtVersion {
    @Property
    public String getName();
    public void setName(String name);

    @Property
    public String getValue();
    public void setValue(String value);
}
