package jw.jzbot.vault;

/**
 * Created by aboyd on 2015-04-08.
 */
public class NoSuchVaultException extends RuntimeException {
    public NoSuchVaultException(String name) {
        super("No such vault: \"" + name + "\"");
    }
}
