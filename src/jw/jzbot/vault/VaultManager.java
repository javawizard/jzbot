package jw.jzbot.vault;

import jw.jzbot.storage.VaultContainer;
import jw.jzbot.storage.VaultStorage;

/**
 * Created by aboyd on 2015-04-08.
 */
public class VaultManager {
    private VaultContainer container;

    public VaultManager(VaultContainer container) {
        this.container = container;
    }

    public Vault getVault(String vaultName) {
        VaultStorage storage = this.container.getVault(vaultName);
        if (storage == null) {
            return null;
        }
        return new Vault(this.container, storage, this);
    }

    public synchronized Vault createVault(String name) {
        // TODO: Don't allow null or the empty string

        if (this.container.getVault(name) != null) {
            throw new VaultAlreadyExistsException(name);
        }

        VaultStorage storage = this.container.createVault();
        storage.setName(name);

        Vault vault = new Vault(this.container, storage, this);
        vault.reset();

        this.container.getVaults().add(storage);

        return vault;
    }

    public synchronized Vault getOrCreateVault(String name) {
        Vault vault = getVault(name);
        if (vault == null) {
            vault = createVault(name);
        }
        return vault;
    }

    public Vault[] listVaults() {
        return this.container.getVaults().isolate().stream()
                .map((storage) -> new Vault(this.container, storage, this))
                .toArray((size) -> new Vault[size]);
    }
}
