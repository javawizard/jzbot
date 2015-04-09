package jw.jzbot.fact.functions.vault;

import jw.jzbot.JZBot;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import jw.jzbot.fact.exceptions.FactoidException;
import jw.jzbot.vault.Vault;

/**
 * Created by aboyd on 2015-04-07.
 */
public class SvgetFunction extends Function {
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context) {
        String vaultName = arguments.resolveString(0);
        String keyName = arguments.resolveString(1);
        Vault vault = JZBot.vaultManager.getVault(vaultName);
        if (vault == null) {
            throw new FactoidException("No such vault: " + vaultName);
        }

        // FIXME: Check against the vault's allowances
        context.updateOldestVaultAccessedVersion(vault.getVersionNumber());

        String value = vault.get(keyName);
        sink.write(value == null ? "" : value);
    }

    @Override
    public String getHelp(String topic) {
        return "Syntax: {svget|<vault>|<key>} -- TBD";
    }
}
