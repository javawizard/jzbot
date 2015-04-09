package jw.jzbot.vault;

import jw.jzbot.JZBot;
import jw.jzbot.storage.MapEntry;
import jw.jzbot.storage.VaultAllowance;
import jw.jzbot.storage.VaultContainer;
import jw.jzbot.storage.VaultStorage;

/**
 * Created by aboyd on 2015-04-08.
 */
public class Vault {
    private VaultContainer container;
    private VaultStorage storage;
    private VaultManager manager;

    public Vault(VaultContainer container, VaultStorage storage, VaultManager manager) {
        this.container = container;
        this.storage = storage;
        this.manager = manager;
    }

    public void reset() {
        this.storage.getEntries().clear();
        this.storage.setVersionNumber(JZBot.newVersionNumber());
    }

    public long getVersionNumber() {
        return this.storage.getVersionNumber();
    }

    public void deleteVault() {
        this.container.getVaults().remove(this);
    }

    public String get(String key) {
        MapEntry entry = this.storage.getEntry(key);
        if (entry == null) {
            return null;
        }
        return entry.getValue();
    }

    public void set(String key, String value) {
        synchronized (this.manager) {
            MapEntry entry = this.storage.getEntry(key);
            if (entry == null) {
                entry = this.storage.createMapEntry();
                entry.setKey(key);
                entry.setValue(value);
                this.storage.getEntries().add(entry);
            }
            entry.setValue(value);
        }
    }

    public void delete(String key) {
        MapEntry entry = this.storage.getEntry(key);
        if (entry != null) {
            this.storage.getEntries().remove(entry);
        }
    }

    public void allow(AllowanceType type, String name) {
        synchronized (this.manager) {
            // TODO: Reset always or just when this factoid/function wasn't already allowed?
            this.reset();

            if (this.storage.getAllowance(type.getType(), name) == null) {
                VaultAllowance allowance = this.storage.createAllowance();
                allowance.setType(type.getType());
                allowance.setName(name);
                this.storage.getAllowances().add(allowance);
            }
        }
    }

    public void deny(AllowanceType type, String name) {
        VaultAllowance allowance = this.storage.getAllowance(type.getType(), name);
        if (allowance != null) {
            this.storage.getAllowances().remove(allowance);
        }
        // Don't reset when we're just removing an allowance
    }

    public boolean isAllowed(AllowanceType type, String name) {
        return this.storage.getAllowance(type.getType(), name) != null;
    }

    public String[] listAllowances(AllowanceType type) {
        return this.storage.getAllowances().isolate().stream()
                .filter((allowance) -> allowance.getType().equals(type.getType()))
                .map((allowance) -> allowance.getName())
                .toArray((size) -> new String[size]);
    }
}
