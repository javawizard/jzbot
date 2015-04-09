package jw.jzbot.storage;

import net.sf.opengroove.common.proxystorage.*;

/**
 * Created by aboyd on 2015-04-07.
 */

@ProxyBean
public interface VaultStorage extends ModifiedAtVersion {
    @Property
    public String getName();
    public void setName(String name);

    @Property
    @ListType(MapEntry.class)
    public StoredList<MapEntry> getEntries();

    @Search(listProperty = "entries", searchProperty = "key")
    public MapEntry getEntry(String key);

    @Constructor
    public MapEntry createMapEntry();

    @Property
    @ListType(VaultAllowance.class)
    public StoredList<VaultAllowance> getAllowances();

    @CompoundSearch(listProperty = "entries", searchProperties = {"type", "name"}, exact = {true, true}, anywhere = {false, false})
    public VaultAllowance getAllowance(String type, String name);

    @Constructor
    public VaultAllowance createAllowance();
}
