package jw.jzbot.vault;

/**
 * Created by aboyd on 2015-04-08.
 */
public class VaultAlreadyExistsException extends RuntimeException {
    public VaultAlreadyExistsException(String name) {
        super("Vault \"" + name + "\" already exists");
    }
}
