package jw.jzbot.help;

import org.apache.commons.lang.ArrayUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by aboyd on 2016-03-28.
 */
public class HelpPageBuilder {
    private String[] path;
    private String content;
    private Map<String, HelpPage> children = new HashMap<>();

    public HelpPageBuilder() {
        this(new String[0]);
    }

    public HelpPageBuilder(String[] path) {
        this.path = path;
    }

    public HelpPageBuilder content(String content) {
        this.content = content;
        return this;
    }

    public HelpPageBuilder child(String name, HelpPage page) {
        this.children.put(name, page);
        return this;
    }

    public HelpPage build() {
        HelpPage page = new DefaultHelpPage(this.content, this.children);

        for (int i = path.length - 1; i >= 0; i--) {
            page = new DefaultHelpPage(null, Collections.singletonMap(path[i], page));
        }

        return page;
    }

    public HelpPage build(String content) {
        this.content = content;
        return build();
    }
}
