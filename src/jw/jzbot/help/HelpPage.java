package jw.jzbot.help;

import java.util.Set;

/**
 * Created by aboyd on 2016-03-28.
 */
public interface HelpPage {
    public String getContent();

    public Set<String> getChildNames();

    public HelpPage getChild(String name);
}
