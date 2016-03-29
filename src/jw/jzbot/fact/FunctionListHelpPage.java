package jw.jzbot.fact;

import jw.jzbot.help.HelpPage;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by aboyd on 2016-03-28.
 */
public class FunctionListHelpPage implements HelpPage {
    public Set<String> getChildNames() {
        Set<String> set = new HashSet<>();
        set.addAll(Arrays.asList(FactParser.getFunctionNames()));
        return set;
    }

    public HelpPage getChild(String name) {
        Function function = FactParser.getFunction(name);

        if (function != null) {
            return function.getHelp();
        } else {
            return null;
        }
    }
}
