package jw.jzbot.help;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by aboyd on 2016-03-28.
 */
public class DefaultHelpPage implements HelpPage {
    private String content;
    private Map<String, HelpPage> children;

    public DefaultHelpPage(String content) {
        this(content, new HashMap<>());
    }

    public DefaultHelpPage(String content, Map<String, HelpPage> children) {
        this.content = content;
        this.children = children;
    }

    public String getContent() {
        return this.content;
    }

    public Set<String> getChildNames() {
        return children.keySet();
    }

    public HelpPage getChild(String name) {
        return children.get(name);
    }
}
