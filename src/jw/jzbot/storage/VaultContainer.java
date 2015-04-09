package jw.jzbot.storage;

import net.sf.opengroove.common.proxystorage.*;

/**
 * Created by aboyd on 2015-04-07.
 */
public interface VaultContainer {
    @Property
    @ListType(VaultStorage.class)
    public StoredList<VaultStorage> getVaults();

    @Search(listProperty = "vaults", searchProperty = "name")
    public VaultStorage getVault(String key);

    @Constructor
    public VaultStorage createVault();
}
