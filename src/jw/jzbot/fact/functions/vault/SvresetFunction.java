package jw.jzbot.fact.functions.vault;

import jw.jzbot.JZBot;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

/**
 * Created by aboyd on 2015-04-09.
 */
public class SvresetFunction extends Function {
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context) {
        String vaultName = arguments.resolveString(0);
        JZBot.vaultManager.getOrCreateVault(vaultName).reset();
    }

    @Override
    public String getHelp(String topic) {
        return "Syntax: {svreset|<vault>} -- TBD";
    }
}
