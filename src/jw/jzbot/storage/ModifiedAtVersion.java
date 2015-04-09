package jw.jzbot.storage;

import net.sf.opengroove.common.proxystorage.Default;
import net.sf.opengroove.common.proxystorage.Property;

/**
 * Created by aboyd on 2015-04-07.
 */
public interface ModifiedAtVersion {
    @Property
    @Default(longValue = 0)
    public long getVersionNumber();
    public void setVersionNumber(long versionNumber);
}
