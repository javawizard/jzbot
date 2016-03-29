package jw.jzbot.help;

import java.util.Collections;
import java.util.Set;

/**
 * Created by aboyd on 2016-03-28.
 */
public interface HelpPage {
    public default String getContent() {
        return null;
    }

    public default Set<String> getChildNames() {
        return Collections.emptySet();
    }

    public default HelpPage getChild(String name) {
        return null;
    }
}
