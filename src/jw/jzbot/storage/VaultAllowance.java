package jw.jzbot.storage;

import net.sf.opengroove.common.proxystorage.Property;
import net.sf.opengroove.common.proxystorage.ProxyBean;

/**
 * Created by aboyd on 2015-04-07.
 */
@ProxyBean
public interface VaultAllowance {
    @Property
    public String getType(); // "factoid" or "function"
    public void setType(String type);

    @Property
    public String getName();
    public void setName(String name);
}
